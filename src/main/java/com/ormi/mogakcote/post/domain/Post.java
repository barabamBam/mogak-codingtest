package com.ormi.mogakcote.post.domain;

import com.ormi.mogakcote.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "post")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, name = "platform_id")
    private Long platformId;

    @Column(nullable = false, name = "problem_number")
    private int problemNumber;

    @Column(nullable = false, name = "language_id")
    private Long languageId;

    @Column(columnDefinition = "TEXT")
    private String code;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(name = "view_cnt")
    private int viewCnt;

    @Embedded
    private PostFlag postFlag;

    @Embedded
    private ReportFlag reportFlag;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public void update(String title, String content, Long platformId, Long languageId, int problemNumber) {
        this.title = title;
        this.content = content;
        this.platformId = platformId;
        this.languageId = languageId;
        this.problemNumber = problemNumber;
    }

    public void incrementViewCount() {
        this.viewCnt++;
    }
}