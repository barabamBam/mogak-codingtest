package com.ormi.mogakcote.news.dto.request;

import com.ormi.mogakcote.news.domain.Type;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewsRequest {

    @NotBlank(message = "알림의 제목은 필수입니다.")
    private String title;

    @NotBlank(message = "알림의 내용은 필수입니다.")
    private String content;

    @NotNull(message = "알림의 종류는 필수입니다.")
    private Type type;

    private boolean hasRelatedContent;

    private Long relatedContentId;

    @NotNull(message = "수신자 지정은 필수입니다.")
    private Long receiverId;
}
