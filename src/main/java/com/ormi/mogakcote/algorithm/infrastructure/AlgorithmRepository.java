package com.ormi.mogakcote.algorithm.infrastructure;

import com.ormi.mogakcote.algorithm.domain.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AlgorithmRepository extends JpaRepository<Algorithm, Integer> {
    Algorithm save(Algorithm algorithm);

    Optional<Algorithm> findByAlgorithmId(Integer algorithmId);
    void deleteByAlgorithmId(Integer algorithmId);
}
