package com.ormi.mogakcote.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostCntResponse {

    private long postCnt;

    public static PostCntResponse toResponse(
            long postCnt
    ) {
        return new PostCntResponse(postCnt);
    }
}
