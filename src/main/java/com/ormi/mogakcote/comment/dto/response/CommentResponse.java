package com.ormi.mogakcote.comment.dto.response;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentResponse {

    private Long id;
    private String nickname;
    private String comment;
    private LocalDateTime createdAt;

    public static CommentResponse toResponse(Long id, String nickname, String comment,
            LocalDateTime createdAt) {
        return new CommentResponse(id, nickname, comment, createdAt);
    }
}
