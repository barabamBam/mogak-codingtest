package com.ormi.mogakcote.report.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportRequest {

    private Long postId;
    private Long platformId;
    private int problemNumber;
    private Long languageId;
    private String code;
}
