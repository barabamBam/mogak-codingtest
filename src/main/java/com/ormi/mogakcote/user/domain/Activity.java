package com.ormi.mogakcote.user.domain;

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

	public void increaseDayCount() {
		this.dayCount++;
	}

	public void decreaseDayCount(LocalDateTime deletePostDate) {
		LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
		if(!deletePostDate.isAfter(now)) {
			this.dayCount = this.dayCount +
				LocalDateTime.of(deletePostDate.getYear(),12,31,0,0).getDayOfYear()
					- deletePostDate.getDayOfYear();
			this.dayCount = this.dayCount +
				now.getDayOfYear()
				- LocalDateTime.of(deletePostDate.getYear(),1,1,0,0).getDayOfYear();

		}
		this.dayCount = 1;
	}

	public void resetDayCount() {
		this.dayCount = 1;
	}
}
