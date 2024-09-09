package com.ormi.mogakcote.post_activity.presentation;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.post_activity.application.VoteService;
import com.ormi.mogakcote.post_activity.dto.response.VoteResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts/{postId}/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<?> clickVote(
            AuthUser user,
            @PathVariable("postId") Long postId) {
        VoteResponse response = voteService.clickVote(user, postId);
        return ResponseDto.ok(response);
    }
}
