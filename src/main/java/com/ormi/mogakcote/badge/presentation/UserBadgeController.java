package com.ormi.mogakcote.badge.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.badge.application.UserBadgeService;
import com.ormi.mogakcote.badge.domain.Badge;
import com.ormi.mogakcote.badge.domain.UserBadge;
import com.ormi.mogakcote.badge.dto.response.UserBadgeResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users/badges")
@RequiredArgsConstructor
public class UserBadgeController {
	private final UserBadgeService userBadgeService;

	@GetMapping
	public ResponseEntity<?> getUserBadges(AuthUser user) {
		List<UserBadgeResponse> userBadges = userBadgeService.getUserBadges(user);

		return ResponseEntity.ok(userBadges);
	}
}
