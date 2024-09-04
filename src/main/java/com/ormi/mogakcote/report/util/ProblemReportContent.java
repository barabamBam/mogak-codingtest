package com.ormi.mogakcote.report.util;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProblemReportContent {

    private String source;

    private String problemAnalysis;

    private String problemApproach;

    private String solutionCode;

    private String solutionAlgorithmName;

    private String timeComplexity;

    private String fullReport;
}
