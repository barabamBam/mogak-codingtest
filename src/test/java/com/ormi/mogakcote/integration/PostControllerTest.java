package com.ormi.mogakcote.integration;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.post.application.PostService;
import com.ormi.mogakcote.post.dto.request.PostRequest;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.problem.domain.Algorithm;
import com.ormi.mogakcote.problem.domain.Language;
import com.ormi.mogakcote.problem.domain.Platform;
import com.ormi.mogakcote.problem.infrastructure.AlgorithmRepository;
import com.ormi.mogakcote.problem.infrastructure.LanguageRepository;
import com.ormi.mogakcote.problem.infrastructure.PlatformRepository;
import com.ormi.mogakcote.security.service.JwtService;
import com.ormi.mogakcote.user.application.UserService;
import com.ormi.mogakcote.user.domain.Activity;
import com.ormi.mogakcote.user.domain.Authority;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("dev")
public class PostControllerTest {

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
    private JwtService jwtService;

    @Autowired
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlatformRepository platformRepository;

    @Autowired
    private AlgorithmRepository algorithmRepository;

    @Autowired
    private LanguageRepository languageRepository;

    private User user;
    private String accessToken;
    private PostRequest postRequest;
    private PostResponse postResponse;

    @BeforeEach
    void setUp() {
        // 유저
        try {
            user = userRepository.save(new User(1L, "userName", "userNickname", "user@test.com", "password", Authority.USER, LocalDateTime.now(), new Activity()));
        } catch (Exception e) {
            user = userRepository.findById(1L).get();
        }

        accessToken = jwtService.generateAccessToken(user);

        // 시스템 유저
        try {
            userRepository.save(new User(2L, "sysName", "sysNickname", "sys@test.com", "password", Authority.SYSTEM, LocalDateTime.now(), new Activity()));
        } catch (Exception e) {
        }

        platformRepository.save(new Platform(1L, "platform"));
        algorithmRepository.save(new Algorithm(1L, "algorithm"));
        languageRepository.save(new Language(1L, "language"));

        // post 추가
        postRequest = new PostRequest();
        postRequest.setTitle("Test title");
        postRequest.setContent("Test content");
        postRequest.setPlatformId(1L);
        postRequest.setProblemNumber(1);
        postRequest.setAlgorithmId(1L);
        postRequest.setLanguageId(1L);
        postRequest.setCode("Hello, world!");
        postRequest.setPublic(true);
        postRequest.setReportRequested(false);

        try {
            postResponse = postService.getPost(1L);
        } catch (Exception e) {
            postResponse = postService.createPost(new AuthUser(1L), postRequest);
        }
    }

    @Test
    void testCreatePost_ok() throws Exception {
        mockMvc.perform(post("/api/v1/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(postResponse.getId()))
                .andExpect(jsonPath("$.data.title").value(postResponse.getTitle()))
                .andExpect(jsonPath("$.data.content").value(postResponse.getContent()));
    }

    @Test
    void testCreatePost_forbidden() throws Exception {
        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetPost_ok() throws Exception {
        mockMvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postResponse.getId()))
                .andExpect(jsonPath("$.title").value(postResponse.getTitle()))
                .andExpect(jsonPath("$.content").value(postResponse.getContent()));
    }

    @Test
    void testGetPost_notfound() throws Exception {
        mockMvc.perform(get("/api/v1/posts/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllPosts() throws Exception {
        mockMvc.perform(get("/api/v1/posts/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(postResponse.getId()))
                .andExpect(jsonPath("$[0].title").value(postResponse.getTitle()))
                .andExpect(jsonPath("$[0].content").value(postResponse.getContent()));
    }

    @Test
    void testModifyPost_ok() throws Exception {
        mockMvc.perform(put("/api/v1/posts/1")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(postResponse.getId()))
                .andExpect(jsonPath("$.data.title").value(postResponse.getTitle()))
                .andExpect(jsonPath("$.data.content").value(postResponse.getContent()));
    }

    @Test
    void testModifyPost_forbidden() throws Exception {
        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testModifyPost_notfound() throws Exception {
        mockMvc.perform(put("/api/v1/posts/2")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletePost_ok() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 삭제 성공"));
    }

    @Test
    void testDeletePost_forbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1"))
                .andExpect(status().isForbidden());
    }
}