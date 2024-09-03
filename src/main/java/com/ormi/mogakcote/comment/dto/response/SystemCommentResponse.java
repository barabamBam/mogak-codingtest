package com.ormi.mogakcote.comment.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SystemCommentResponse {

    private Long id;
    private String nickname;
    private String problemReport;
    private String codeReport;
    private LocalDateTime createdAt;

    public static SystemCommentResponse toResponse(
            Long id, String nickname, String problemReport, String codeReport,
            LocalDateTime createdAt
    ) {
        return new SystemCommentResponse(id, nickname, problemReport, codeReport, createdAt);
    }
}
