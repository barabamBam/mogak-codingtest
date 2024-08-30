package com.ormi.mogakcote.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NoticeRequest {

    private Long adminId;

    @NotBlank(message = "공지사항 제목은 필수입니다")
    private String title;

    @NotBlank(message = "공지사항 내용은 필수입니다.")
    private String content;
}
