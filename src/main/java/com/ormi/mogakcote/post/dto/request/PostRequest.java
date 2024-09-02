package com.ormi.mogakcote.post.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PostRequest {
    private String title;
    private String content;
    private Long platformId;
    private int problemNumber;
    private List<Long> algorithmIds;
    private Long languageId;
    private String code;
    private boolean isPublic;
    private boolean isReportRequested;
}