package com.ormi.mogakcote.user.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Activity {
	@Column(name = "comment_count")
	private int commentCount = 0;

	@Column(name = "day_count")
	private int dayCount = 1;

	public void increaseCommentCount() {
		this.commentCount++;
	}

	public void decreaseCommentCount() {
		if(this.commentCount > 0) this.commentCount--;
		else this.commentCount = 0;
	}

	public void increaseDayCount(LocalDate prevPostDate, LocalDate createPostDate) {
		// 오늘 한번 작성했으면 더이상 오늘 날짜에는 count 하지 않음
		if(prevPostDate != createPostDate) this.dayCount++;
	}

	public void decreaseDayCount(LocalDateTime deletePostDate) {
		LocalDate now = LocalDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();
		// 지우려는 게시글이 오늘 날짜보다 이전일 경우 실행
		if(deletePostDate.toLocalDate().isBefore(now)) {
			// 예를 들어, 오늘이 2024-01-10이고 지우려는 게시글의 날짜가 2023-12-25인 경우
			// dayCount = (365-359)+(10-1+1) = 6+10 = 16일 째 게시글을 연속으로 쓴 게 된다.
			this.dayCount =
				(
					(LocalDateTime.of(deletePostDate.getYear(), 12, 31, 0, 0).toLocalDate().getDayOfYear()
					- deletePostDate.toLocalDate().getDayOfYear())
				+
					(now.getDayOfYear()
					- LocalDateTime.of(deletePostDate.getYear(), 1, 1, 0, 0).toLocalDate().getDayOfYear()+1)
				);
			if(this.dayCount < 0) this.dayCount = 0;
		}
		else if(deletePostDate.toLocalDate().isEqual(now)) this.dayCount--;
		else this.dayCount = 0;
	}

	public void resetDayCount() {
		this.dayCount = 1;
	}

}
