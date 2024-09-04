package com.ormi.mogakcote.post.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.post.dto.request.PostRequest;
import com.ormi.mogakcote.notice.dto.response.NoticeResponse;
import com.ormi.mogakcote.post.application.PostService;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.post.dto.request.PostSearchRequest;
import com.ormi.mogakcote.post.dto.response.PostSearchResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

	@GetMapping("/list")
	public ResponseEntity<?> mainPosts(
		AuthUser user,
		 @ModelAttribute PostSearchRequest postSearchRequest
	) {
		List<NoticeResponse> noticeResponse = postService.getNoticeLatestFive();
		Page<PostSearchResponse> postResponse = postService.searchPost(user, postSearchRequest);

		Map<String, Object> map = new HashMap<>();
		map.put("notice", noticeResponse);
		map.put("postResponse", postResponse);

		return ResponseDto.ok(map);
	}

    @PostMapping
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

    @GetMapping
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
        postService.deletePost(user,postId);
        return ResponseEntity.ok(new SuccessResponse("게시글 삭제 성공"));
    }

}