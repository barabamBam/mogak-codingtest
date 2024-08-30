package com.ormi.mogakcote.algorithm.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Algorithm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer algorithmId;

    @Column(nullable = false)
    private String name;

    public void update(Integer algorithmId, String algorithmName) {
        this.algorithmId = algorithmId;
        this.name = algorithmName;
    }
}
