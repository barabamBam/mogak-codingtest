package com.ormi.mogakcote.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ormi.mogakcote.auth.dto.request.EmailWrapper;
import com.ormi.mogakcote.auth.dto.request.LoginRequest;
import com.ormi.mogakcote.auth.dto.request.PasswordResetRequest;
import com.ormi.mogakcote.auth.infrastructure.PasswordResetService;
import com.ormi.mogakcote.email.service.EmailService;
import com.ormi.mogakcote.security.TokenConstants;
import com.ormi.mogakcote.security.model.AccessTokenWrapper;
import com.ormi.mogakcote.security.model.AuthorizeToken;
import com.ormi.mogakcote.security.service.JwtService;
import com.ormi.mogakcote.user.domain.Activity;
import com.ormi.mogakcote.user.domain.Authority;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("dev")
public class AuthControllerTest {

    @Container
    static MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("testdb")
            .withUsername("testUser")
            .withPassword("password");

    @Container
    static GenericContainer<?> redis = new GenericContainer(DockerImageName.parse("redis:6-alpine"))
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
        registry.add("spring.cache.host", redis::getHost);
        registry.add("spring.cache.port", redis::getFirstMappedPort);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private PasswordResetService resetService;

    @MockBean
    private EmailService emailService;

    private User user;

    @BeforeEach
    void setUp() {
        user = userRepository.save(new User(1L, "testUser", "testNickname", "test@example.com",
                passwordEncoder.encode("password123"),
                Authority.USER, LocalDateTime.now(), new Activity()));
    }

    @Test
    void testLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(user.getEmail());
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(TokenConstants.REFRESH_TOKEN_COOKIE_NAME))
                .andExpect(jsonPath("$.access_token").exists());
    }

    @Test
    void testRefresh() throws Exception {
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.getRefreshToken();
        jwtService.setTokenPair(new AuthorizeToken(accessToken, refreshToken));

        AccessTokenWrapper wrapper = new AccessTokenWrapper(accessToken);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrapper))
                        .cookie(new Cookie(TokenConstants.REFRESH_TOKEN_COOKIE_NAME, refreshToken)))
                .andExpect(status().isOk())
                .andExpect(cookie().exists(TokenConstants.REFRESH_TOKEN_COOKIE_NAME))
                .andExpect(jsonPath("$.access_token").exists());
    }

    @Test
    void testFindEmail() throws Exception {
        mockMvc.perform(get("/api/v1/auth/email?name=" + user.getName() + "&nickname=" + user.getNickname())
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void testSendPasswordResetMail() throws Exception {
        EmailWrapper emailWrapper = new EmailWrapper();
        emailWrapper.setEmail(user.getEmail());

        when(resetService.register(anyString())).thenReturn("resetCode");

        mockMvc.perform(post("/api/v1/auth/reset-password-mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailWrapper)))
                .andExpect(status().isOk());
    }

    @Test
    void testResetPassword() throws Exception {
        PasswordResetRequest request = new PasswordResetRequest();
        request.setEmail(user.getEmail());
        request.setCode("resetCode");

        when(resetService.reset(anyString(), anyString())).thenReturn("newPassword");

        mockMvc.perform(post("/api/v1/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}