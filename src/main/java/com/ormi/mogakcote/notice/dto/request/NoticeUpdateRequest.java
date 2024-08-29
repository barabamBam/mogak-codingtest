package com.ormi.mogakcote.notice.dto.request;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NoticeUpdateRequest {
    private Long noticeId;
    private Long adminId;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
}
