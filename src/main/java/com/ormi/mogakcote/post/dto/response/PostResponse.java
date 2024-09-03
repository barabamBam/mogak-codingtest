package com.ormi.mogakcote.post.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private Long platformId;
    private int problemNumber;
    private List<Long> algorithmIds;
    private Long languageId;
    private String code;
    private boolean isPublic;
    private boolean isReportRequested;
    private int viewCnt;
    private boolean isBanned;

    public static PostResponse toResponse(
            Long id,
            String title,
            String content,
            Long platformId,
            int problemNumber,
            List<Long> algorithmIds,
            Long languageId,
            String code,
            boolean isPublic,
            boolean isReportRequested,
            int viewCnt,
            boolean isBanned
    ) {
        return new PostResponse(id, title, content, platformId, problemNumber, algorithmIds, languageId, code, isPublic, isReportRequested, viewCnt, isBanned);
    }
}