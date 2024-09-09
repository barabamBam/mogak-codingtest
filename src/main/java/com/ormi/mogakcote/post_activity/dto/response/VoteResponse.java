package com.ormi.mogakcote.post_activity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class VoteResponse {
    private int voteCnt;

    public static VoteResponse toResponse(int voteCnt) {
        return new VoteResponse(voteCnt);
    }
}
