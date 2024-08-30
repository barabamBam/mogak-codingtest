package com.ormi.mogakcote.post;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ormi.mogakcote.auth.model.AuthUser;

public interface PostRepositoryCustom {
	Page<PostResponse> searchPosts(AuthUser user, PostSearchRequest postSearchRequest, Pageable pageable);
}
