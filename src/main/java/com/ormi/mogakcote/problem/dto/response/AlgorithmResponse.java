package com.ormi.mogakcote.problem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AlgorithmResponse {
    private Long id;
    private String algorithmName;

    public static AlgorithmResponse toResponse(Long id, String algorithmName){
        return new AlgorithmResponse(id, algorithmName);
    }
}
