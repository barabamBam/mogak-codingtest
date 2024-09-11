package com.ormi.mogakcote.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @Size(max = 400, message = "댓글은 최대 400자 까지 작성 가능합니다.")
    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
