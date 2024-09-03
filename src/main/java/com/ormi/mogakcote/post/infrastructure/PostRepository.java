package com.ormi.mogakcote.post.infrastructure;

import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ormi.mogakcote.post.infrastructure.PostRepositoryCustom;
import com.ormi.mogakcote.post.domain.Post;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

	@Query("select p.voteCnt from Post p where p.id = ?1")
	Integer findVoteCountById(Long id);

	@Query("select (count(p) > 0) from Post p where p.createdAt = ?1")
	boolean existsPostByCreatedAt(LocalDate date);
}
