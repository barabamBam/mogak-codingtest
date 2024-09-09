package com.ormi.mogakcote.post_activity.infrastructure;

import com.ormi.mogakcote.post_activity.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    List<Vote> findByUserId(Long userId);

    List<Vote> findByPostId(Long postId);

    long countByPostId(Long postId);

    // 특정 사용자가 특정 게시물에 투표했는지 확인
    boolean existsByUserIdAndPostId(Long userId, Long postId);

    // 특정 사용자가 특정 게시물에 한 투표 삭제
    void deleteByUserIdAndPostId(Long userId, Long postId);

    // 특정 게시물의 모든 투표 삭제
    void deleteAllByPostId(Long postId);
}