package com.ormi.mogakcote.badge.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserBadgeRequest {

	private Long userId;
	private Long badgeId;
}
