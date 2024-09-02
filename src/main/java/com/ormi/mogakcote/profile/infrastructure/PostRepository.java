package com.ormi.mogakcote.profile.infrastructure;

import com.ormi.mogakcote.profile.entity.Post;
import com.ormi.mogakcote.profile.entity.Users;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserOrderByCreatedAtDesc(Users user);

    @Query("SELECT p FROM Post p WHERE p.user = :user ORDER BY SIZE(p.likes) DESC")
    List<Post> findTop3ByUserOrderByLikesDesc(@Param("user") Users user, Pageable pageable);

    long countByUser(Users user);
}