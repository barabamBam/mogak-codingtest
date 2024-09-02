package com.ormi.mogakcote.post.presentation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.notice.dto.response.NoticeResponse;
import com.ormi.mogakcote.post.application.PostService;
import com.ormi.mogakcote.post.dto.request.PostSearchRequest;
import com.ormi.mogakcote.post.dto.response.PostSearchResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {
	private final PostService postService;

	@GetMapping
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


}
