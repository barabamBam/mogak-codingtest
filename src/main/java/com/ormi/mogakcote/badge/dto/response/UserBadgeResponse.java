package com.ormi.mogakcote.badge.dto.response;

import java.time.LocalDateTime;

import com.ormi.mogakcote.badge.domain.UserBadge;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserBadgeResponse {

	private Long id;
	private LocalDateTime acquiredAt;
	private String name;
	private String description;

	public static UserBadgeResponse toResponse(
		Long id, LocalDateTime acquiredAt, String name, String description) {
		return new UserBadgeResponse(id, acquiredAt, name, description);
	}
}
