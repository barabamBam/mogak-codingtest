package com.ormi.mogakcote.post.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.post.dto.request.PostSearchRequest;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.post.dto.response.PostSearchResponse;

public interface PostRepositoryCustom {
	Page<PostSearchResponse> searchPosts(AuthUser user, PostSearchRequest postSearchRequest, Pageable pageable);
}
