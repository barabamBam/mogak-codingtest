package com.ormi.mogakcote.post.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostResponseWithNickname {

    private Long id;
    private String userNickname;
    private String title;
    private String content;
    private String platformName;
    private int problemNumber;
    private String algorithmName;
    private String languageName;
    private String code;
    private boolean isPublic;
    private boolean isReportRequested;
    private int viewCnt;
    private int voteCnt;
    private boolean isBanned;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static PostResponseWithNickname toResponse(
            Long id,
            String userNickname,
            String title,
            String content,
            String platformName,
            int problemNumber,
            String algorithmName,
            String languageName,
            String code,
            boolean isPublic,
            boolean isReportRequested,
            int viewCnt,
            int voteCnt,
            boolean isBanned,
            LocalDateTime createdAt,
            LocalDateTime modifiedAt

    ) {
        return new PostResponseWithNickname(id, userNickname, title, content, platformName, problemNumber,
                algorithmName, languageName, code, isPublic, isReportRequested, viewCnt, voteCnt, isBanned, createdAt, modifiedAt);
    }
}