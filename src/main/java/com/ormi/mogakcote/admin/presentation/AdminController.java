package com.ormi.mogakcote.admin.presentation;

import com.ormi.mogakcote.post.application.PostService;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {

    private final PostService postService;
    @PutMapping("/api/v1/admin/convertBanned/{postId}")
    public ResponseEntity<?> convertBanned(
            @PathVariable(name = "postId") Long id
    ) {
        PostResponse updateBanned = postService.convertBanned(id);
        return ResponseEntity.ok(updateBanned);
    }
}
