package com.ormi.mogakcote.report.infrastructure;

import com.ormi.mogakcote.report.domain.CodeReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface CodeReportRepository extends JpaRepository<CodeReport, Long> {

    CodeReport findByPostId(Long postId);
}
