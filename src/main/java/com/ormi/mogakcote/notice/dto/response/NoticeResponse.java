package com.ormi.mogakcote.notice.dto.response;

import com.ormi.mogakcote.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class NoticeResponse {
    private Long noticeId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long adminId;

    public static NoticeResponse toResponse(Long noticeId, String title, String content, LocalDateTime createdAt, LocalDateTime updatedAt, Long adminId) {
        return new NoticeResponse(noticeId, title, content, createdAt, updatedAt, adminId);
    }
}
