package com.ormi.mogakcote.profile.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostProfileInfoResponse {
    private Long postId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private int viewCnt;

    public static PostProfileInfoResponse toResponse(
            Long postId,
            String title,
            String content,
            LocalDateTime createdAt,
            int viewCnt
    ) {
        return new PostProfileInfoResponse(postId, title, content, createdAt, viewCnt);
    }
}
