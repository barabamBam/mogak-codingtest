package com.ormi.mogakcote.unit.post;

import static org.junit.jupiter.api.Assertions.*;

import com.ormi.mogakcote.post.infrastructure.PostRepositoryImpl;
import com.ormi.mogakcote.unit.config.TestQueryDslConfig;
import com.ormi.mogakcote.user.domain.Authority;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.domain.PostFlag;
import com.ormi.mogakcote.post.domain.ReportFlag;
import com.ormi.mogakcote.post.dto.request.PostSearchRequest;
import com.ormi.mogakcote.post.dto.response.PostSearchResponse;
import com.ormi.mogakcote.problem.domain.Algorithm;
import com.ormi.mogakcote.problem.domain.Language;
import com.ormi.mogakcote.problem.domain.PostAlgorithm;
import com.ormi.mogakcote.user.domain.User;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

@DataJpaTest
@Import({PostRepositoryImpl.class, TestQueryDslConfig.class})
@ActiveProfiles("unittest")
@Testcontainers
public class PostRepositoryImplTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private PostRepositoryImpl postRepositoryImpl;

    private AuthUser authUser;

    @BeforeEach
    void setUp() {
        User user = User.builder()
                .name("userName")
                .nickname("userNickname")
                .email("user@test.com")
                .password("password")
                .authority(Authority.USER)
                .build();
        entityManager.persist(user);
        authUser = new AuthUser(user.getId());

        Algorithm algorithm = new Algorithm(null, "DFS");
        entityManager.persist(algorithm);

        Language language = new Language(null, "Java");
        entityManager.persist(language);

        PostFlag publicFlag = PostFlag.builder().isPublic(true).isSuccess(true).isBanned(false).build();
        ReportFlag reportFlag = ReportFlag.builder().isReportRequested(false).hasPreviousReportRequested(false).build();

        Post publicPost = Post.builder()
                .title("Public Post")
                .content("This is a public post")
                .platformId(1L)
                .problemNumber(1234)
                .languageId(language.getId())
                .code("public class Solution { }")
                .postFlag(publicFlag)
                .reportFlag(reportFlag)
                .viewCnt(10)
                .voteCnt(5)
                .userId(user.getId())
                .build();
        entityManager.persist(publicPost);

        PostAlgorithm postAlgorithm = new PostAlgorithm(null, publicPost.getId(), algorithm.getId());
        entityManager.persist(postAlgorithm);

        PostFlag privateFlag = PostFlag.builder().isPublic(false).isSuccess(false).isBanned(false).build();
        Post privatePost = Post.builder()
                .title("Private Post")
                .content("This is a private post")
                .platformId(1L)
                .problemNumber(5678)
                .languageId(language.getId())
                .code("public class PrivateSolution { }")
                .postFlag(privateFlag)
                .reportFlag(reportFlag)
                .viewCnt(5)
                .voteCnt(2)
                .userId(user.getId())
                .build();
        entityManager.persist(privatePost);

        entityManager.flush();
    }

    @Test
    void searchPosts_PublicPostsOnly() {
        PostSearchRequest request = new PostSearchRequest();
        request.setPage(1);

        Page<PostSearchResponse> result = postRepositoryImpl.searchPosts(null, request, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Public Post", result.getContent().get(0).getTitle());
    }

    @Test
    void searchPosts_IncludePrivatePosts() {
        PostSearchRequest request = new PostSearchRequest();
        request.setPage(1);

        Page<PostSearchResponse> result = postRepositoryImpl.searchPosts(authUser, request, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().anyMatch(post -> post.getTitle().equals("Private Post")));
    }

    @Test
    void searchPosts_ByKeyword() {
        PostSearchRequest request = new PostSearchRequest();
        request.setKeyword("public");
        request.setPage(1);

        Page<PostSearchResponse> result = postRepositoryImpl.searchPosts(authUser, request, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Public Post", result.getContent().get(0).getTitle());
    }

    @Test
    void searchPosts_ByAlgorithm() {
        PostSearchRequest request = new PostSearchRequest();
        request.setAlgorithm("DFS");
        request.setPage(1);

        Page<PostSearchResponse> result = postRepositoryImpl.searchPosts(authUser, request, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Public Post", result.getContent().get(0).getTitle());
    }

    @Test
    void searchPosts_ByLanguage() {
        PostSearchRequest request = new PostSearchRequest();
        request.setLanguage("Java");
        request.setPage(1);

        Page<PostSearchResponse> result = postRepositoryImpl.searchPosts(authUser, request, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void searchPosts_SuccessfulPostsOnly() {
        PostSearchRequest request = new PostSearchRequest();
        request.setCheckSuccess(true);
        request.setPage(1);

        Page<PostSearchResponse> result = postRepositoryImpl.searchPosts(authUser, request, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Public Post", result.getContent().get(0).getTitle());
    }

    @Test
    void searchPosts_SortByMostViewed() {
        PostSearchRequest request = new PostSearchRequest();
        request.setSortBy("MOST_VIEWED");
        request.setPage(1);

        Page<PostSearchResponse> result = postRepositoryImpl.searchPosts(authUser, request, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals("Public Post", result.getContent().get(0).getTitle());
    }
}