package com.ormi.mogakcote.post.domain;

import com.ormi.mogakcote.common.entity.BaseEntity;
import com.ormi.mogakcote.profile.vote.Like;
import com.ormi.mogakcote.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;


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
    private boolean isPublic;


    @Column(nullable = false)
    private boolean isBanned;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


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