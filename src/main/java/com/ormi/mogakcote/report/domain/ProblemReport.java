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
public class ProblemReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;

    @Column(name = "full_report", columnDefinition = "TEXT")
    private String fullReport;

    @Column(name = "problem_analysis", columnDefinition = "TEXT")
    private String problemAnalysis;

    @Column(name = "problem_approach", columnDefinition = "TEXT")
    private String problemApproach;

    @Column(name = "solution_code", columnDefinition = "TEXT")
    private String solutionCode;

    @Column(name = "time_complexity")
    private String timeComplexity;

    @Column(name = "problem_number")
    private int problemNumber;

    @Column(name = "platform_id")
    private Long platformId;

    @Column(name = "language_id")
    private Long languageId;
}
