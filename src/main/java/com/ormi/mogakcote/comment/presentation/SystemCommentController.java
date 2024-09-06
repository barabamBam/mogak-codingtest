package com.ormi.mogakcote.comment.presentation;

import static com.ormi.mogakcote.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.ormi.mogakcote.comment.application.SystemCommentService;
import com.ormi.mogakcote.common.model.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequestMapping(path = "/api/v1/posts/{postId}/system-comments")
@RequiredArgsConstructor
public class SystemCommentController {
    private final SystemCommentService systemCommentService;

    @GetMapping
    public ResponseEntity<?> getSystemComment(
            @PathVariable("postId") Long postId
    ) {
        var responses = systemCommentService.getSystemComment(postId);
        return ResponseDto.ok(responses);
    }
}
