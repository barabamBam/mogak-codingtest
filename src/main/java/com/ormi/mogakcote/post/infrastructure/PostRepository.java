package com.ormi.mogakcote.post.infrastructure;

import java.time.LocalDate;

import com.ormi.mogakcote.post.domain.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT p FROM Post p WHERE p.userId = :userId ORDER BY p.viewCnt DESC")
    List<Post> findTop3ByUserIdOrderByViewsDesc(@Param("userId") Long userId, Pageable pageable);

    long countByUserId(Long userId);

	@Query("select p.voteCnt from Post p where p.id = ?1")
	Integer findVoteCountById(Long id);

	@Query("select (count(p) > 0) from Post p where p.createdAt = ?1")
	boolean existsPostByCreatedAt(LocalDate date);
}