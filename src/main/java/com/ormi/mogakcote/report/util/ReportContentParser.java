package com.ormi.mogakcote.report.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReportContentParser {

    public CodeReportContent parseAnswerForCode(String content) {
        log.info("*** code report content = {}", content);
        return CodeReportContent.builder()
                .isPassed(extractBetween(content, "[성공/실패 여부] ", " <END>").equals("true"))
                .codeAnalysis((extractBetween(content, "[코드 개선점 제안] ","<END>")))
                .fullReport(content)
                .build();
    }

    public ProblemReportContent parseAnswerForProblem(String content) {
        log.info("*** problem report content = {}", content);
        return ProblemReportContent.builder()
                // 문제 분석
                .source(extractBetween(content, "[해당 문제로 바로가는 url] ", "<END>"))
                .problemAnalysis(extractBetween(content, "[문제 분석] ", "<END>"))
                .problemApproach(extractBetween(content, "[풀이 접근] ", "<END>"))
                .solutionCode(extractBetween(content, "[해결하는 코드]\n", "<END>"))
                .solutionAlgorithmName(extractBetween(content, "[사용된 알고리즘] ", "<END>"))
                .timeComplexity(extractBetween(content, "[해결 코드의 시간복잡도] ", "<END>"))
                .fullReport(content)
                .build();
    }

    private static String extractBetween(String content, String startMarker, String endMarker) {
        int startIndex = content.indexOf(startMarker);
        if (startIndex == -1) {
            return "";
        }
        startIndex += startMarker.length();
        int endIndex = content.indexOf(endMarker, startIndex);
        if (endIndex == -1) {
            return content.substring(startIndex).trim();
        }
        return content.substring(startIndex, endIndex).trim();
    }

}