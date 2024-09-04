package com.ormi.mogakcote.post.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class PostRequest {
    private String title;
    private String content;
    private Long platformId;
    private int problemNumber;
    private Long algorithmId;
    private Long languageId;
    private String code;

    @JsonProperty("isPublic")
    private boolean isPublic;

    @JsonProperty("isReportRequested")
    private boolean isReportRequested;

    @JsonProperty("hasPreviousReportRequested")
    private boolean hasPreviousReportRequested;
}