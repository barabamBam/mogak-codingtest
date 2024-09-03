package com.ormi.mogakcote.problem.infrastructure;

import com.ormi.mogakcote.problem.domain.Algorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AlgorithmRepository extends JpaRepository<Algorithm, Long> {

    Optional<Algorithm> findById(Long id);
    void deleteById(Long id);

    @Query("select a.name from Algorithm a where a.id = ?1")
    String findNameById(Long id);
}
