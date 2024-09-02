package com.ormi.mogakcote.problem.infrastructure;

import com.ormi.mogakcote.problem.domain.PostAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostAlgorithmRepository extends JpaRepository<PostAlgorithm, Long> {
    List<PostAlgorithm> findByPostId(Long postId);
    void deleteByPostId(Long postId);
}