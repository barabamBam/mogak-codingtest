package com.ormi.mogakcote.comment.application;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.badge.application.UserBadgeService;
import com.ormi.mogakcote.comment.domain.Comment;
import com.ormi.mogakcote.comment.dto.request.CommentRequest;
import com.ormi.mogakcote.comment.dto.response.CommentResponse;
import com.ormi.mogakcote.comment.infrastructure.CommentRepository;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.auth.AuthInvalidException;
import com.ormi.mogakcote.exception.comment.CommentInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.post.PostInvalidException;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.user.application.UserService;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;
    private final UserBadgeService userBadgeService;
    private final UserRepository userRepository;

    /**
     * 답변 생성
     */
    @Transactional
    public CommentResponse createComment(AuthUser user, Long postId, CommentRequest request) {
        throwsIfPostNotExist(postId);

        Comment comment = buildComment(request, user.getId(), postId);
        Comment savedComment = commentRepository.save(comment);

        userService.updateActivity(user.getId(), "increaseComment");

        userBadgeService.makeUserBadge(user, "COMMENT");

        return CommentResponse.toResponse(
                savedComment.getId(),
                getNicknameOrThrowIfNotExist(savedComment),
                savedComment.getContent(),
                savedComment.getCreatedAt()
        );
    }

    /**
     * 답변 수정
     */
    @Transactional
    public CommentResponse editComment(AuthUser user, Long postId, Long commentId,
            CommentRequest request) {
        throwsIfPostNotExist(postId);

        String nickname = "tester";   // TODO user 정보

        Comment findComment = getCommentById(commentId);

        validateSameUser(findComment.getUserId(), user.getId());

        findComment.update(
                request.getContent()
        );

        return CommentResponse.toResponse(
                findComment.getId(),
                nickname,
                findComment.getContent(),
                findComment.getCreatedAt()
        );
    }

    /**
     * 답변 삭제
     */
    @Transactional
    public SuccessResponse deleteComment(AuthUser user, Long postId, Long commentId) {
        throwsIfPostNotExist(postId);

        Comment findComment = getCommentById(commentId);

        validateSameUser(findComment.getUserId(), user.getId());

        commentRepository.deleteById(findComment.getId());

        userService.updateActivity(user.getId(), "decreaseComment");

        return new SuccessResponse("답변 삭제를 성공했습니다.");
    }

    /**
     * 답변 전체 목록
     */
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentList(Long postId) {
        throwsIfPostNotExist(postId);

        List<CommentResponse> commentResponses = new ArrayList<>();
        List<Comment> findComments = commentRepository.findAllByPostId(postId);

        findComments.forEach(findComment -> {
            commentResponses.add(CommentResponse.toResponse(
                    findComment.getId(),
                    getNicknameOrThrowIfNotExist(findComment),
                    findComment.getContent(),
                    findComment.getCreatedAt()
            ));
        });

        return commentResponses;
    }

    private void throwsIfPostNotExist(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostInvalidException(ErrorType.POST_NOT_FOUND_ERROR);
        }
    }

    private static Comment buildComment(CommentRequest request, Long userId, Long postId) {
        return Comment.builder()
                .content(request.getContent())
                .userId(userId)
                .postId(postId)
                .build();
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new CommentInvalidException(ErrorType.COMMENT_NOT_FOUND_ERROR)
        );
    }

    private static void validateSameUser(Long commentUserId, Long userId) {
        if (!commentUserId.equals(userId)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }

    private String getNicknameOrThrowIfNotExist(Comment comment) {
        return userRepository.findById(comment.getUserId()).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        ).getNickname();
    }
}
