package com.ormi.mogakcote.unit.post;

import static com.ormi.mogakcote.security.TokenConstants.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import com.ormi.mogakcote.post.presentation.PostController;
import com.ormi.mogakcote.security.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.orchestration.application.ReportCreationOrchestrator;
import com.ormi.mogakcote.notice.application.NoticeService;
import com.ormi.mogakcote.post.application.PostService;
import com.ormi.mogakcote.post.dto.request.PostRequest;
import com.ormi.mogakcote.post.dto.request.PostSearchRequest;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.post.dto.response.PostSearchResponse;

@WebMvcTest(PostController.class)
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("unittest")
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @MockBean
    private ReportCreationOrchestrator reportCreationOrchestrator;

    @MockBean
    private NoticeService noticeService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    private AuthUser mockUser;
    private PostRequest mockPostRequest;
    private PostResponse mockPostResponse;
    private PostSearchResponse mockPostSearchResponse;

    @BeforeEach
    void setUp() {
        mockUser = new AuthUser(1L);
        mockPostRequest = new PostRequest();
        mockPostRequest.setTitle("Test Title");
        mockPostRequest.setContent("Test Content");
        mockPostRequest.setPlatformId(1L);
        mockPostRequest.setProblemNumber(1234);
        mockPostRequest.setAlgorithmId(1L);
        mockPostRequest.setLanguageId(1L);
        mockPostRequest.setCode("Test Code");
        mockPostRequest.setPublic(true);
        mockPostRequest.setReportRequested(true);
        mockPostRequest.setHasPreviousReportRequested(false);

        mockPostResponse = PostResponse.toResponse(
                1L, "Test Title", "Test Content", 1L, 1234, 1L, 1L,
                "Test Code", true, true, 0, false
        );

        mockPostSearchResponse = PostSearchResponse.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .algorithms(List.of("Algorithm1"))
                .nickname("testUser")
                .viewCnt(0)
                .createdAt(LocalDateTime.now())
                .build();

        Claims claims = Jwts.claims()
                .add(USER_ID_CLAIM, 1L)
                .add(USER_AUTHORITY_CLAIM, "USER")
                .add(USER_EMAIL_CLAIM, "test@test.com")
                .subject("userName")
                .build();

        when(jwtService.getUserId(any())).thenReturn(mockUser.getId());
        when(jwtService.validateAccessToken(any())).thenReturn(claims);
    }

    @Test
    void createPost_ShouldReturnCreatedResponse() throws Exception {
        when(reportCreationOrchestrator.createPostWithReportAndComment(any(), any()))
                .thenReturn(mockPostResponse);

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockPostRequest))
                        .requestAttr("authUser", mockUser)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(mockPostResponse.getId()))
                .andExpect(jsonPath("$.data.title").value(mockPostResponse.getTitle()))
                .andExpect(jsonPath("$.data.platformId").value(mockPostResponse.getPlatformId()))
                .andExpect(jsonPath("$.data.problemNumber").value(mockPostResponse.getProblemNumber()))
                .andExpect(jsonPath("$.data.algorithmIds").value(mockPostResponse.getAlgorithmIds()))
                .andExpect(jsonPath("$.data.languageId").value(mockPostResponse.getLanguageId()))
                .andExpect(jsonPath("$.data.public").value(mockPostResponse.isPublic()))
                .andExpect(jsonPath("$.data.reportRequested").value(mockPostResponse.isReportRequested()));
    }

    @Test
    void getPost_ShouldReturnPost() throws Exception {
        when(postService.getPost(1L)).thenReturn(mockPostResponse);

        mockMvc.perform(get("/api/v1/posts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockPostResponse.getId()))
                .andExpect(jsonPath("$.title").value(mockPostResponse.getTitle()))
                .andExpect(jsonPath("$.platformId").value(mockPostResponse.getPlatformId()))
                .andExpect(jsonPath("$.problemNumber").value(mockPostResponse.getProblemNumber()))
                .andExpect(jsonPath("$.algorithmIds").value(mockPostResponse.getAlgorithmIds()))
                .andExpect(jsonPath("$.languageId").value(mockPostResponse.getLanguageId()))
                .andExpect(jsonPath("$.public").value(mockPostResponse.isPublic()))
                .andExpect(jsonPath("$.reportRequested").value(mockPostResponse.isReportRequested()));
    }

    @Test
    void getAllPosts_ShouldReturnListOfPosts() throws Exception {
        List<PostResponse> posts = Collections.singletonList(mockPostResponse);
        when(postService.getAllPosts()).thenReturn(posts);

        mockMvc.perform(get("/api/v1/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(mockPostResponse.getId()))
                .andExpect(jsonPath("$[0].title").value(mockPostResponse.getTitle()))
                .andExpect(jsonPath("$[0].platformId").value(mockPostResponse.getPlatformId()))
                .andExpect(jsonPath("$[0].problemNumber").value(mockPostResponse.getProblemNumber()))
                .andExpect(jsonPath("$[0].algorithmIds").value(mockPostResponse.getAlgorithmIds()))
                .andExpect(jsonPath("$[0].languageId").value(mockPostResponse.getLanguageId()))
                .andExpect(jsonPath("$[0].public").value(mockPostResponse.isPublic()))
                .andExpect(jsonPath("$[0].reportRequested").value(mockPostResponse.isReportRequested()));
    }

    @Test
    void modifyPost_ShouldReturnUpdatedPost() throws Exception {
        when(reportCreationOrchestrator.updatePostWithReportAndComment(any(), eq(1L), any()))
                .thenReturn(mockPostResponse);

        mockMvc.perform(put("/api/v1/posts/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockPostRequest))
                        .requestAttr("authUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockPostResponse.getId()))
                .andExpect(jsonPath("$.title").value(mockPostResponse.getTitle()))
                .andExpect(jsonPath("$.platformId").value(mockPostResponse.getPlatformId()))
                .andExpect(jsonPath("$.problemNumber").value(mockPostResponse.getProblemNumber()))
                .andExpect(jsonPath("$.algorithmIds").value(mockPostResponse.getAlgorithmIds()))
                .andExpect(jsonPath("$.languageId").value(mockPostResponse.getLanguageId()))
                .andExpect(jsonPath("$.public").value(mockPostResponse.isPublic()))
                .andExpect(jsonPath("$.reportRequested").value(mockPostResponse.isReportRequested()));
    }

    @Test
    void deletePost_ShouldReturnSuccessResponse() throws Exception {
        mockMvc.perform(delete("/api/v1/posts/1")
                        .requestAttr("authUser", mockUser))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 삭제 성공"));
    }

    @Test
    void mainPosts_ShouldReturnModelAndView() throws Exception {
        PostSearchRequest searchRequest = new PostSearchRequest();
        Page<PostSearchResponse> postPage = new PageImpl<>(Collections.singletonList(mockPostSearchResponse));

        when(noticeService.getNoticeLatestFive()).thenReturn(List.of());
        when(postService.searchPost(any(AuthUser.class), any(PostSearchRequest.class))).thenReturn(postPage);

        mockMvc.perform(get("/api/v1/posts/list")
                        .requestAttr("authUser", mockUser)
                        .flashAttr("postSearchRequest", searchRequest))
                .andExpect(status().isOk())
                .andExpect(view().name("post/list"))
                .andExpect(model().attributeExists("notices", "posts", "postSearchRequest"));
    }
}