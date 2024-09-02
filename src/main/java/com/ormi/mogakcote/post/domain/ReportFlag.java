package com.ormi.mogakcote.post.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportFlag {

    private boolean isReportRequested;
    private boolean hasPreviousReportRequested;
}