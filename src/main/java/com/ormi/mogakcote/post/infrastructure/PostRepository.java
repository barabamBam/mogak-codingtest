package com.ormi.mogakcote.post.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ormi.mogakcote.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {


}
