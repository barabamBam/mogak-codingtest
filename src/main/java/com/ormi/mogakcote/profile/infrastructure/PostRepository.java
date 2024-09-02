package com.ormi.mogakcote.profile.infrastructure;

import com.ormi.mogakcote.profile.vote.Post;
import com.ormi.mogakcote.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserOrderByCreatedAtDesc(User user);

    @Query("SELECT p FROM Post p WHERE p.user = :user ORDER BY SIZE(p.likes) DESC")
    List<Post> findTop3ByUserOrderByLikesDesc(@Param("user") User user, Pageable pageable);

    long countByUser(User user);
}