package com.ormi.mogakcote.report.infrastructure;

import com.ormi.mogakcote.report.domain.ProblemReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface ProblemReportRepository extends JpaRepository<ProblemReport, Long> {

    ProblemReport findByPlatformIdAndProblemNumberAndLanguageId(
            Long platformId, int problemNumber, Long languageId);
}
