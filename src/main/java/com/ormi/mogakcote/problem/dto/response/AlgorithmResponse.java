package com.ormi.mogakcote.problem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlgorithmResponse {
    private Long algorithmId;
    private String algorithmName;

    public static AlgorithmResponse toResponse(Long algorithmId, String algorithmName){
        return new AlgorithmResponse(algorithmId, algorithmName);
    }
}
