package com.ormi.mogakcote.comment.presentation;

import static com.ormi.mogakcote.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.comment.application.CommentService;
import com.ormi.mogakcote.comment.dto.request.CommentRequest;
import com.ormi.mogakcote.comment.dto.response.CommentResponse;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.exception.rate_limit.PerSecondRateLimitExceededException;
import com.ormi.mogakcote.rate_limiter.annotation.RateLimit;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/v1/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    @RateLimit(key = "'createComment:' + #user.id", limit = 5, period = 1,
            exceptionClass = PerSecondRateLimitExceededException.class)
    public ResponseEntity<?> createComment(
            AuthUser user,
            @PathVariable("postId") Long postId,
            @RequestBody @Valid CommentRequest request
    ) {
        var response = commentService.createComment(user, postId, request);
        return ResponseDto.created(response);
    }

    // TODO 수정 버튼 누르자마자 동일인물 검증하는 로직 추가하기 + 프론트에도

    @PutMapping(path = "/{commentId}")
    public ResponseEntity<?> updateComment(
            AuthUser user,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid CommentRequest request
    ) {
        var response = commentService.editComment(user, postId, commentId, request);
        return ResponseDto.ok(response);

    }

    @DeleteMapping(path = "/{commentId}")
    public ResponseEntity<?> deleteComment(
            AuthUser user,
            @PathVariable("postId") Long postId,
            @PathVariable("commentId") Long commentId
    ) {
        var response = commentService.deleteComment(user, postId, commentId);
        return ResponseDto.ok(response);

    }

    @GetMapping(path = "/list")
    public ResponseEntity<?> getCommentList(
            @PathVariable("postId") Long postId
    ) {
        List<CommentResponse> responses = commentService.getCommentList(postId);
        return ResponseDto.ok(responses);
    }
}
