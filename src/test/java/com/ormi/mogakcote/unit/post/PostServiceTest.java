package com.ormi.mogakcote.unit.post;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.ormi.mogakcote.post.application.PostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.badge.application.UserBadgeService;
import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.domain.PostFlag;
import com.ormi.mogakcote.post.domain.ReportFlag;
import com.ormi.mogakcote.post.dto.request.PostRequest;
import com.ormi.mogakcote.post.dto.request.PostSearchRequest;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.post.dto.response.PostSearchResponse;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.problem.domain.PostAlgorithm;
import com.ormi.mogakcote.problem.infrastructure.PostAlgorithmRepository;
import com.ormi.mogakcote.user.application.UserService;
import org.springframework.test.context.ActiveProfiles;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("unittest")
public class PostServiceTest {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostAlgorithmRepository postAlgorithmRepository;

    @Mock
    private UserService userService;

    @Mock
    private UserBadgeService userBadgeService;

    private AuthUser authUser;
    private PostRequest postRequest;
    private Post post;
    private PostAlgorithm postAlgorithm;

    @BeforeEach
    void setUp() {
        authUser = new AuthUser(1L);
        postRequest = new PostRequest();
        postRequest.setTitle("Test Title");
        postRequest.setContent("Test Content");
        postRequest.setPlatformId(1L);
        postRequest.setProblemNumber(1234);
        postRequest.setAlgorithmId(1L);
        postRequest.setLanguageId(1L);
        postRequest.setCode("Test Code");
        postRequest.setPublic(true);
        postRequest.setReportRequested(false);

        PostFlag postFlag = PostFlag.builder().isPublic(true).isSuccess(false).isBanned(false).build();
        ReportFlag reportFlag = ReportFlag.builder().isReportRequested(false).hasPreviousReportRequested(false).build();

        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .platformId(1L)
                .problemNumber(1234)
                .languageId(1L)
                .code("Test Code")
                .postFlag(postFlag)
                .reportFlag(reportFlag)
                .viewCnt(0)
                .voteCnt(0)
                .userId(1L)
                .build();

        postAlgorithm = PostAlgorithm.builder().postId(1L).algorithmId(1L).build();
    }

    @Test
    void createPost_Success() {
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postAlgorithmRepository.save(any(PostAlgorithm.class))).thenReturn(postAlgorithm);
        when(postRepository.existsPostByCreatedAt(any())).thenReturn(true);

        PostResponse response = postService.createPost(authUser, postRequest);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        assertEquals("Test Content", response.getContent());

        verify(userService).updateActivity(eq(1L), eq("increaseDay"));
        verify(userBadgeService).makeUserBadge(eq(authUser), eq("POST"));
    }

    @Test
    void getPost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postAlgorithmRepository.findByPostId(1L)).thenReturn(postAlgorithm);

        PostResponse response = postService.getPost(1L);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());
        assertEquals(1, response.getViewCnt());

        verify(postRepository).save(post);
    }

    @Test
    void updatePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postAlgorithmRepository.findByPostId(1L)).thenReturn(postAlgorithm);

        PostResponse response = postService.updatePost(authUser, 1L, postRequest);

        assertNotNull(response);
        assertEquals("Test Title", response.getTitle());

        verify(postAlgorithmRepository).deleteByPostId(1L);
        verify(postAlgorithmRepository).save(any(PostAlgorithm.class));
    }

    @Test
    void deletePost_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));

        assertDoesNotThrow(() -> postService.deletePost(authUser, 1L));

        verify(postAlgorithmRepository).deleteByPostId(1L);
        verify(postRepository).deleteById(1L);
        verify(userService).updateActivity(eq(1L), eq("decreaseDay"), any());
    }

    @Test
    void searchPost_Success() {
        PostSearchRequest searchRequest = new PostSearchRequest();
        searchRequest.setPage(1);

        PostSearchResponse searchResponse = PostSearchResponse.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .build();

        Page<PostSearchResponse> page = new PageImpl<>(Collections.singletonList(searchResponse));

        when(postRepository.searchPosts(eq(authUser), any(), any())).thenReturn(page);

        Page<PostSearchResponse> result = postService.searchPost(authUser, searchRequest);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Test Title", result.getContent().get(0).getTitle());
    }

    @Test
    void convertBanned_Success() {
        when(postRepository.findById(1L)).thenReturn(Optional.of(post));
        when(postAlgorithmRepository.findByPostId(1L)).thenReturn(postAlgorithm);

        PostResponse response = postService.convertBanned(1L);

        assertNotNull(response);
        assertTrue(response.isBanned());

        verify(postRepository).save(post);
    }
}