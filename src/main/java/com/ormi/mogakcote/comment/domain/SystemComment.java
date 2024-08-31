package com.ormi.mogakcote.comment.domain;

import com.ormi.mogakcote.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_report", columnDefinition = "TEXT")
    private String codeReport;

    @Column(name = "problem_report", columnDefinition = "TEXT")
    private String problemReport;

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "user_id")
    private Long userId;
}
