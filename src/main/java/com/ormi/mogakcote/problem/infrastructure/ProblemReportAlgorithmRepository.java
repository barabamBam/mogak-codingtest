package com.ormi.mogakcote.problem.infrastructure;

import com.ormi.mogakcote.problem.domain.ProblemReportAlgorithm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ProblemReportAlgorithmRepository extends
        JpaRepository<ProblemReportAlgorithm, Long> {

    boolean existsByAlgorithmIdAndProblemReportId(Long algorithmId, Long problemReportId);
}