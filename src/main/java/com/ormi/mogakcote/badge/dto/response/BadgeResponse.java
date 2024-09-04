package com.ormi.mogakcote.badge.dto.response;

import com.ormi.mogakcote.badge.domain.Badge;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BadgeResponse {
	private Long badgeId;
	private String name;
	private String description;

	public static BadgeResponse toResponse(
		Long id, String name, String description) {
		return new BadgeResponse(id, name, description);
	}

}
