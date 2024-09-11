package com.ormi.mogakcote.orchestration.application;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.comment.application.SystemCommentService;
import com.ormi.mogakcote.comment.dto.request.SystemCommentRequest;
import com.ormi.mogakcote.exception.async.AsyncException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.post.PostInvalidException;
import com.ormi.mogakcote.news.domain.Type;
import com.ormi.mogakcote.news.dto.request.NewsRequest;
import com.ormi.mogakcote.news.application.NewsService;
import com.ormi.mogakcote.post.application.PostService;
import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.dto.request.PostRequest;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.report.application.ReportService;
import com.ormi.mogakcote.report.dto.request.ReportRequest;
import com.ormi.mogakcote.report.dto.response.ReportResponse;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportCreationOrchestrator {

    private final PostService postService;
    private final ReportService reportService;
    private final SystemCommentService systemCommentService;
    private final NewsService newsService;
    private final PostRepository postRepository;

    @Transactional
    public PostResponse createPostWithReportAndComment(AuthUser user, PostRequest request) {

        // 게시글 작성
        PostResponse postResponse = postService.createPost(user, request);
        Post savedPost = getPost(postResponse);

        // 리포트 요청 시 비동기 처리, 비동기 작업 즉시 반환
        if (request.isReportRequested()) {
            CompletableFuture.runAsync(() -> createReportAndSystemCommentAndNews(user, savedPost));
        }

        return PostResponse.toResponse(
                savedPost.getId(), savedPost.getTitle(), savedPost.getContent(),
                savedPost.getPlatformId(), savedPost.getProblemNumber(), request.getAlgorithmId(),
                savedPost.getLanguageId(), savedPost.getCode(), savedPost.getPostFlag().isPublic(),
                savedPost.getReportFlag().isReportRequested(), savedPost.getViewCnt(),
                savedPost.getVoteCnt(),
                savedPost.getPostFlag()
                        .isBanned()
        );
    }

    @Transactional
    public PostResponse updatePostWithReportAndComment(AuthUser user, Long postId,
            PostRequest request) {

        // 게시글 수정
        PostResponse postResponse = postService.updatePost(user, postId, request);
        Post savedPost = getPost(postResponse);

        // 이전에 리포트를 요청한 적 없는데 게시글을 수정하며 새로 요청하는 경우
        if (request.isReportRequested() && !request.isHasPreviousReportRequested()) {
            CompletableFuture.runAsync(() -> createReportAndSystemCommentAndNews(user, savedPost));
        }

        return PostResponse.toResponse(
                savedPost.getId(), savedPost.getTitle(), savedPost.getContent(),
                savedPost.getPlatformId(), savedPost.getProblemNumber(), request.getAlgorithmId(),
                savedPost.getLanguageId(), savedPost.getCode(), savedPost.getPostFlag().isPublic(),
                savedPost.getReportFlag().isReportRequested(), savedPost.getViewCnt(),
                savedPost.getVoteCnt(),
                savedPost.getPostFlag()
                        .isBanned()
        );
    }

    // 비동기 작업의 경우 @Async 로 하였을 때, 메인 트랜잭션이 완료되기 전에 비동기 작업이 실행될 수 있어서 트랜잭션의 경계를 관리한다.
    // 따라서 해당 메서드가 참조되는 메서드의 Transactional 과 별도의 트랜잭션으로 분리한다.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void createReportAndSystemCommentAndNews(AuthUser user, Post post) {
        try {
            // 분석 요청
            ReportResponse reportResponse = reportService.createReport(
                    user, buildReportRequest(post));
            // 시스템 댓글 등록
            systemCommentService.createSystemComment(
                    buildSystemCommentRequest(post.getId(), reportResponse));
            // 알림 전송
            newsService.createNews(buildNewsRequest(post));
        } catch (Exception e) {
            throw new AsyncException(ErrorType.ASYNC_ERROR);
        }
    }

    private Post getPost(PostResponse postResponse) {
        return postRepository.findById(postResponse.getId()).orElseThrow(
                () -> new PostInvalidException(ErrorType.POST_NOT_FOUND_ERROR)
        );
    }

    private static ReportRequest buildReportRequest(Post savedPost) {
        return ReportRequest.builder()
                .postId(savedPost.getId())
                .platformId(savedPost.getPlatformId())
                .problemNumber(savedPost.getProblemNumber())
                .languageId(savedPost.getLanguageId())
                .code(savedPost.getCode())
                .build();
    }

    private static SystemCommentRequest buildSystemCommentRequest(Long postId,
            ReportResponse reportResponse) {
        return SystemCommentRequest.builder()
                .postId(postId)
                .codeReport(reportResponse.getCodeReport())
                .problemReport(reportResponse.getProblemReport())
                .build();
    }

    private static NewsRequest buildNewsRequest(Post savedPost) {
        return NewsRequest.builder()
                .title("시스템 댓글이 등록되었습니다!")
                .content("확인해보세요!")
                .type(Type.COMMENT)
                .hasRelatedContent(true)
                .relatedContentId(savedPost.getId())
                .receiverId(savedPost.getUserId())
                .build();
    }

}
