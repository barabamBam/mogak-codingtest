package com.ormi.mogakcote.report.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportResponse {

    private Long codeReportId;
    private String codeReport;
    private Long problemReportId;
    private String problemReport;

    public static ReportResponse toResponse(Long codeReportId, String codeReport, Long problemReportId, String problemReport) {
        return new ReportResponse(codeReportId, codeReport, problemReportId, problemReport);
    }
}
