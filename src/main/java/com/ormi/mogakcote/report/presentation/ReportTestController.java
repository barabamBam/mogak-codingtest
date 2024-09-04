package com.ormi.mogakcote.report.presentation;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.report.application.ReportService;
import com.ormi.mogakcote.report.dto.request.ReportRequest;
import com.ormi.mogakcote.report.dto.response.ReportResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/reports")
@RequiredArgsConstructor
public class ReportTestController {

    private final ReportService reportService;

    @PostMapping
    public ResponseEntity<?> reportTest(
            AuthUser user,
            @RequestBody @Valid ReportRequest request
    ) {
        ReportResponse response = reportService.createReport(user, request);
        return ResponseDto.created(response);
    }
}
