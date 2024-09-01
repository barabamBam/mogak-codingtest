package com.ormi.mogakcote.algorithm.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlgorithmResponse {
    private Integer algorithmId;
    private String algorithmName;

    public static AlgorithmResponse toResponse(Integer algorithmId, String algorithmName){
        return new AlgorithmResponse(algorithmId, algorithmName);
    }
}
