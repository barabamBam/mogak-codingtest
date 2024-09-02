package com.ormi.mogakcote.badge.dto;


public enum BadgeType {
	COMMENT_20, // 댓글을 20개 이상 작성
	COMMENT_50, // 댓글을 50개 이상 작성
	COMMENT_100, // 댓글을 100개 이상 작성

	VOTE_100, // 추천을 100개 이상 받음
	VOTE_500, //추천을 500개 이상 받음
	VOTE_1000, //추천을 1000개 이상 벋움

	 POST_MONTH, // 한달간 꾸준히 글을 작성
	 POST_HALF, // 반 년간 꾸준히 글을 작성
	 POST_YEAR // 일 년간 꾸준히 글을 작성
}
