package com.ormi.mogakcote.post.application;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.domain.PostFlag;
import com.ormi.mogakcote.post.domain.ReportFlag;
import com.ormi.mogakcote.post.dto.request.PostRequest;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.post.exception.AuthInvalidException;
import com.ormi.mogakcote.post.exception.PostInvalidException;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.problem.domain.PostAlgorithm;
import com.ormi.mogakcote.problem.infrastructure.PostAlgorithmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final PostAlgorithmRepository postAlgorithmRepository;

    @Transactional
    public PostResponse createPost(AuthUser user, PostRequest request) {
        Post savedPost = buildAndSavePost(user.getId(), request);


        List<Long> algorithmIds = savePostAlgorithms(savedPost.getId(), request.getAlgorithmIds());

        return PostResponse.toResponse(
            savedPost.getId(),
            savedPost.getTitle(),
            savedPost.getContent(),
            savedPost.getPlatformId(),
            savedPost.getProblemNumber(),
            algorithmIds,
            savedPost.getLanguageId(),
            savedPost.getCode(),
            savedPost.getPostFlag().isPublic(),
            savedPost.getReportFlag().isReportRequested(),
            savedPost.getViewCnt(),
            savedPost.getPostFlag().isBanned()
        );
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long postId) {
        Post post = getPostById(postId);
        List<Long> algorithmIds = getAlgorithmIds(postId);

        post.incrementViewCount();
        postRepository.save(post);

        return PostResponse.toResponse(
            post.getId(),
            post.getTitle(),
            post.getContent(),
            post.getPlatformId(),
            post.getProblemNumber(),
            algorithmIds,
            post.getLanguageId(),
            post.getCode(),
            post.getPostFlag().isPublic(),
            post.getReportFlag().isReportRequested(),
            post.getViewCnt(),
            post.getPostFlag().isBanned()
        );
    }

    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        return posts.stream()
            .map(post -> {
                List<Long> algorithmIds = getAlgorithmIds(post.getId());
                return PostResponse.toResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getPlatformId(),
                    post.getProblemNumber(),
                    algorithmIds,
                    post.getLanguageId(),
                    post.getCode(),
                    post.getPostFlag().isPublic(),
                    post.getReportFlag().isReportRequested(),
                    post.getViewCnt(),
                    post.getPostFlag().isBanned()
                );
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse updatePost(AuthUser user, Long postId, PostRequest request) {
        Post post = getPostById(postId);
        validateSameUser(post.getUserId(), user.getId());

        post.update(
            request.getTitle(),
            request.getContent(),
            request.getPlatformId(),
            request.getLanguageId(),
            request.getProblemNumber()
        );

        Post updatedPost = postRepository.save(post);

        List<Long> algorithmIds = updatePostAlgorithms(postId, request.getAlgorithmIds());

        return PostResponse.toResponse(
            updatedPost.getId(),
            updatedPost.getTitle(),
            updatedPost.getContent(),
            updatedPost.getPlatformId(),
            updatedPost.getProblemNumber(),
            algorithmIds,
            updatedPost.getLanguageId(),
            updatedPost.getCode(),
            updatedPost.getPostFlag().isPublic(),
            updatedPost.getReportFlag().isReportRequested(),
            updatedPost.getViewCnt(),
            updatedPost.getPostFlag().isBanned()
        );
    }

    @Transactional
    public SuccessResponse deletePost(AuthUser user, Long postId) {
        Post post = getPostById(postId);

        validateSameUser(post.getUserId(), user.getId());

        postAlgorithmRepository.deleteByPostId(postId);
        postRepository.deleteById(postId);

        return new SuccessResponse("게시글 삭제 성공");
    }

    private Post buildAndSavePost(Long userId, PostRequest request) {
        log.info("userId = {}", userId);
        PostFlag postFlag = PostFlag.builder()
            .isPublic(request.isPublic())
            .isSuccess(false)
            .isBanned(false)
            .build();
        ReportFlag reportFlag = ReportFlag.builder()
            .isReportRequested(request.isReportRequested())
            .hasPreviousReportRequested(false)
            .build();
        Post post = Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .platformId(request.getPlatformId())
            .problemNumber(request.getProblemNumber())
            .languageId(request.getLanguageId())
            .code(request.getCode())
            .postFlag(postFlag)
            .reportFlag(reportFlag)
            .viewCnt(0)
            .userId(userId)
            .build();
        return postRepository.save(post);
    }

    private Post getPostById(Long postId) {
        return postRepository.findById(postId).orElseThrow(
            () -> new PostInvalidException(ErrorType.POST_NOT_FOUND_ERROR)
        );
    }

    private static void validateSameUser(Long postUserId, Long userId) {
        if (!postUserId.equals(userId)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }

    private List<Long> savePostAlgorithms(Long postId, List<Long> algorithmIds) {
        return algorithmIds.stream()
            .map(id -> {
                PostAlgorithm postAlgorithm = PostAlgorithm.builder()
                    .postId(postId)
                    .algorithmId(id)
                    .build();
                return postAlgorithmRepository.save(postAlgorithm);
            })
            .map(PostAlgorithm::getAlgorithmId)
            .collect(Collectors.toList());
    }

    private List<Long> getAlgorithmIds(Long postId) {
        return postAlgorithmRepository.findByPostId(postId).stream()
            .map(PostAlgorithm::getAlgorithmId)
            .collect(Collectors.toList());
    }

    private List<Long> updatePostAlgorithms(Long postId, List<Long> newAlgorithmIds) {
        postAlgorithmRepository.deleteByPostId(postId);
        return savePostAlgorithms(postId, newAlgorithmIds);
    }

    @Transactional
    public PostResponse convertBanned(Long id) {
        Post findPost = getPostById(id);
        List<Long> algorithmIds = getAlgorithmIds(id);
        if (findPost.getPostFlag().isBanned()){
            findPost.updateBanned(false);
        }else {
            findPost.updateBanned(true);
        }

        return PostResponse.toResponse(
                findPost.getId(),
                findPost.getTitle(),
                findPost.getContent(),
                findPost.getPlatformId(),
                findPost.getProblemNumber(),
                algorithmIds,
                findPost.getLanguageId(),
                findPost.getCode(),
                findPost.getPostFlag().isPublic(),
                findPost.getReportFlag().isReportRequested(),
                findPost.getViewCnt(),
                findPost.getPostFlag().isBanned()
        );
    }
}