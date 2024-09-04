package com.ormi.mogakcote.post.presentation;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.post.dto.request.PostRequest;
import com.ormi.mogakcote.post.application.PostService;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PostMapping()
    public ResponseEntity<?> writePost(
            AuthUser user,
            @RequestBody PostRequest request
    ) {
        var response = postService.createPost(user, request);
        return ResponseDto.created(response);
    }


    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable(name = "postId") Long postId) {
        PostResponse post = postService.getPost(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<?> modifyPost(AuthUser user, @PathVariable(name = "postId") Long postId, @RequestBody PostRequest postRequest) {
        PostResponse updatedPost = postService.updatePost(user, postId, postRequest);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<SuccessResponse> deletePost(AuthUser user, @PathVariable(name = "postId") Long postId) {
        postService.deletePost(user, postId);
        return ResponseEntity.ok(new SuccessResponse("게시글 삭제 성공"));
    }

}