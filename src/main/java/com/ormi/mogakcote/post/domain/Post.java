package com.ormi.mogakcote.post.domain;

import com.ormi.mogakcote.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

  // 알고리즘의 경우 problem 패키지 안의(현재 작업 중) Algorithm 도메인을 이용할 예정.

  @Column(columnDefinition = "TEXT")
  private String code;

  @Column(nullable = false, name = "user_id")
  private Long userId;

  @Column(name = "view_cnt")
  private int viewCnt;

  @Column(name = "vote_cnt")
  private int voteCnt;

  @Column(name = "prob_report_id")
  private Long probReportId;

  @Embedded
  private PostFlag postFlag;

  @Embedded
  private ReportFlag reportFlag;

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

  public void decrementViewCount() {
    if (this.viewCnt >= 0) this.viewCnt--;
    else this.viewCnt = 0;
  }

  public void incrementVoteCount() {
    this.voteCnt++;
  }

  public void decrementVoteCount() {
    if (this.voteCnt >= 0) this.voteCnt--;
    else this.voteCnt = 0;
  }

  public void updateBanned(boolean isBanned) {
      if (this.postFlag == null) {
          this.postFlag = new PostFlag(); // 기본 값으로 초기화
      }
      this.postFlag = PostFlag.builder()
              .isPublic(this.postFlag.isPublic())
              .isSuccess(this.postFlag.isSuccess())
              .isBanned(isBanned)
              .build();
  }
}
