package com.ormi.mogakcote.integration;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.domain.PostFlag;
import com.ormi.mogakcote.post.domain.ReportFlag;
import com.ormi.mogakcote.profile.application.UserProfileService;
import com.ormi.mogakcote.security.service.JwtService;
import com.ormi.mogakcote.user.domain.Activity;
import com.ormi.mogakcote.user.domain.Authority;
import com.ormi.mogakcote.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("dev")
public class UserProfileControllerTest {

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

    @MockBean
    private UserProfileService userProfileService;

    @Autowired
    private JwtService jwtService;

    private User user;
    private String accessToken;

    @BeforeEach
    void setUp() {
        user = new User(1L, "testUser", "testNickname", "test@example.com",
                "password123", Authority.USER, LocalDateTime.now(), new Activity());
        accessToken = jwtService.generateAccessToken(user);

        when(userProfileService.getUserProfile("testNickname")).thenReturn(user);
    }

    @Test
    void testGetUserProfile() throws Exception {
        Post post1 = new Post(1L, "title1", "content1", 1L, 00, 1L, "", 1L, 0, 0, 1L, PostFlag.builder().build(), ReportFlag.builder().build());

        Post post2 = new Post(2L, "title2", "content2", 1L, 00, 1L, "", 1L, 0, 0, 1L, PostFlag.builder().build(), ReportFlag.builder().build());

        when(userProfileService.getUserPosts(user)).thenReturn(Arrays.asList(post1, post2));
        when(userProfileService.getTotalPostCount(user)).thenReturn(2L);
        when(userProfileService.getTopLikedPosts(user)).thenReturn(Arrays.asList(post1));

        mockMvc.perform(get("/api/v1/profile/testNickname")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.nickname").value("testNickname"))
                .andExpect(jsonPath("$.posts.length()").value(2))
                .andExpect(jsonPath("$.totalPosts").value(2))
                .andExpect(jsonPath("$.topLikedPosts.length()").value(1));
    }

    @Test
    void testUpdateProfile() throws Exception {
        Map<String, String> updateData = new HashMap<>();
        updateData.put("nickname", "updatedNickname");
        updateData.put("name", "Updated Name");
        updateData.put("email", "updated@example.com");
        updateData.put("password", "newpassword123");

        User updatedUser = new User(1L, "Updated Name", "updatedNickname", "updated@example.com",
                "newpassword123", Authority.USER, LocalDateTime.now(), new Activity());

        when(userProfileService.updateProfile("updatedNickname", "Updated Name", "updated@example.com", "newpassword123"))
                .thenReturn(updatedUser);

        mockMvc.perform(post("/api/v1/profile/edit")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.nickname").value("updatedNickname"))
                .andExpect(jsonPath("$.message").value("프로필이 성공적으로 업데이트되었습니다."));
    }

    @Test
    void testDeleteAccount() throws Exception {
        mockMvc.perform(delete("/api/v1/profile/testNickname")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("계정이 성공적으로 삭제되었습니다."));

        verify(userProfileService, times(1)).deleteAccount("testNickname");
    }

    @Test
    void testGetEditProfilePage() throws Exception {
        mockMvc.perform(get("/api/v1/profile/edit/testNickname")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.nickname").value("testNickname"));
    }

    @Test
    void testGetUserProfileNotFound() throws Exception {
        when(userProfileService.getUserProfile("nonexistentUser")).thenReturn(null);

        mockMvc.perform(get("/api/v1/profile/nonexistentUser")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }
}