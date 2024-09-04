package com.ormi.mogakcote.post.infrastructure;

import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ormi.mogakcote.post.domain.Post;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT p FROM Post p WHERE p.userId = :userId ORDER BY p.viewCnt DESC")
    List<Post> findTop3ByUserIdOrderByViewsDesc(@Param("userId") Long userId, Pageable pageable);

    long countByUserId(Long userId);
}