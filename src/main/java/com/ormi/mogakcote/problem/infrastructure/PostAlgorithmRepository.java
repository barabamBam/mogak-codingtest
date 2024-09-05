package com.ormi.mogakcote.problem.infrastructure;

import com.ormi.mogakcote.problem.domain.PostAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface PostAlgorithmRepository extends JpaRepository<PostAlgorithm, Long> {
    PostAlgorithm findByPostId(Long postId);

    void deleteByPostId(Long postId);

    @Query("select p.algorithmId from PostAlgorithm p where p.postId = ?1")
    Long findAlgorithmIdByPostId(Long id);
}