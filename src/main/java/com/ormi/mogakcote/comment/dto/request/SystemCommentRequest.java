package com.ormi.mogakcote.comment.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SystemCommentRequest {

    private Long postId;
    private String codeReport;
    private String problemReport;
}
