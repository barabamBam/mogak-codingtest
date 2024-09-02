package com.ormi.mogakcote.problem.infrastructure;

import com.ormi.mogakcote.problem.domain.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface AlgorithmRepository extends JpaRepository<Algorithm, Long> {
    Optional<Algorithm> findByAlgorithmId(Long algorithmId);
    void deleteByAlgorithmId(Long algorithmId);
}
