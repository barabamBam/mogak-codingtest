package com.ormi.mogakcote.post.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ormi.mogakcote.post.domain.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

}