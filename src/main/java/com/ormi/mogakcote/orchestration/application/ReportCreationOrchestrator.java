package com.ormi.mogakcote.orchestration.application;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.comment.application.SystemCommentService;
import com.ormi.mogakcote.comment.dto.request.SystemCommentRequest;
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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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

    /**
     * 게시글 생성히먀 최초 보고서 요청 시(게시글 수정 시 코드는 수정 불가능하게!)
     */
    @Transactional
    public PostResponse createPostWithReportAndComment(AuthUser user,
            PostRequest request) {

        // 게시글 작성
        PostResponse postResponse = postService.createPost(user, request);
        Post savedPost = getPost(postResponse);

        if (request.isReportRequested()) {

            // 분석 요청
            ReportResponse reportResponse = reportService.createReport(
                    user, buildReportRequest(savedPost));

            // 시스템 댓글 등록
            systemCommentService.createSystemComment(
                    buildSystemCommentRequest(savedPost.getId(), reportResponse));

            // 알림 전송
            newsService.createNews(buildNewsRequest(savedPost));

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

    /**
     * 게시글 엔티티에 시스템 댓글이 이미 등록된 적 있는지 체크하는 플래그 필드 등을 추가해서, 2회차부터는 조회를 하도록!
     * TODO 게시글에 hasPreviousReportRequested 필드 추가 후에, 이거는 일단 true 가 되면 isReportRequested 에 상관없이 쭉 true 로 유지.
     *  그리고 게시글 수정 컨트롤러에서  hasPreviousReportRequested 가 false 인데, isReportRequested 가 true 다?(=수정할 때 최초 리포트 요청) createPostWithReportAndComment 호출
     *                               나머지의 경우(hasPreviousReportRequested 가 true 등) 평범한 commentService 의 수정 메서드 호출.
     */

    @Transactional
    public PostResponse updatePostWithReportAndComment(AuthUser user, Long postId,
            PostRequest request) {
        // 게시글 수정
        PostResponse postResponse = postService.updatePost(user, postId, request);
        Post savedPost = getPost(postResponse);

        // 이전에 리포트를 요청한 적 없는데 게시글을 수정하며 새로 요청하는 경우
        if (request.isReportRequested() && !request.isHasPreviousReportRequested()) {
            // 분석 요청
            ReportResponse reportResponse = reportService.createReport(
                    user, buildReportRequest(savedPost));
            // 시스템 댓글 등록
            systemCommentService.createSystemComment(
                    buildSystemCommentRequest(savedPost.getId(), reportResponse));
            // 수정 시에는 알림을 전송하지 않는다.
        }
        // 이전에 리포트를 요청한 적이 있고, 다시 요청하는 경우 이미 존재하는 시스템 댓글을 불러오기만 한다.

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
