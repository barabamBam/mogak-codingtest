package com.ormi.mogakcote.report.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_report", columnDefinition = "TEXT")
    private String fullReport;

    @Column(name = "is_passed")
    private boolean isPassed;

    @Column(name = "code_analysis", columnDefinition = "TEXT")
    private String codeAnalysis;

    @Column(name = "post_id")
    private Long postId;
}
