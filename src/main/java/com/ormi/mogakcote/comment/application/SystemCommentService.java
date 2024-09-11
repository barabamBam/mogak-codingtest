package com.ormi.mogakcote.comment.application;

import com.ormi.mogakcote.comment.domain.SystemComment;
import com.ormi.mogakcote.comment.dto.request.SystemCommentRequest;
import com.ormi.mogakcote.comment.dto.response.SystemCommentResponse;
import com.ormi.mogakcote.comment.infrastructure.SystemCommentRepository;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.post.PostInvalidException;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.user.domain.Authority;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SystemCommentService {

    private final SystemCommentRepository systemCommentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    /**
     * 시스템 댓글 생성
     */
    @Transactional
    public void createSystemComment(SystemCommentRequest systemCommentRequest) {
        throwsIfPostNotExist(systemCommentRequest.getPostId());
        buildAndSaveSystemComment(systemCommentRequest);
    }

    /**
     * 시스템 댓글 조회
     */
    @Transactional(readOnly = true)
    public SystemCommentResponse getSystemComment(Long postId) {
        throwsIfPostNotExist(postId);

        SystemComment findSystemComment = systemCommentRepository.findByPostId(postId);
        if(findSystemComment == null) {
            return null;
        }
        User systemUser = userRepository.findById(findSystemComment.getUserId()).orElseThrow(
                () -> new UserInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR)
        );

        return SystemCommentResponse.toResponse(
                findSystemComment.getId(),
                systemUser.getNickname(),
                findSystemComment.getCodeReport(),
                findSystemComment.getProblemReport(),
                findSystemComment.getCreatedAt()
        );
    }


    private void throwsIfPostNotExist(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new PostInvalidException(ErrorType.POST_NOT_FOUND_ERROR);
        }
    }

    private void buildAndSaveSystemComment(SystemCommentRequest systemCommentRequest) {
        User systemUser = userRepository.findByAuthority(Authority.SYSTEM).orElseThrow(
                () -> new UserInvalidException(ErrorType.SYSTEM_USER_NOT_FOUND_ERROR)
        );

        SystemComment systemComment = SystemComment.builder()
                .codeReport(systemCommentRequest.getCodeReport())
                .problemReport(systemCommentRequest.getProblemReport())
                .postId(systemCommentRequest.getPostId())
                .userId(systemUser.getId())
                .build();

        systemCommentRepository.save(systemComment);
    }
}
