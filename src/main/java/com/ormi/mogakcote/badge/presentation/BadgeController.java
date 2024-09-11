package com.ormi.mogakcote.badge.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ormi.mogakcote.badge.application.BadgeService;
import com.ormi.mogakcote.badge.domain.Badge;
import com.ormi.mogakcote.badge.dto.request.BadgeRequest;
import com.ormi.mogakcote.badge.dto.response.BadgeResponse;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.common.model.ResponseDto;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path="/api/v1/admin/badge")
@RequiredArgsConstructor
public class BadgeController {

	private final BadgeService badgeService;

	@GetMapping
	public ResponseEntity<?> getBadges()
	{
		List<BadgeResponse> response = badgeService.getBadges();
		return ResponseDto.ok(response);

	}

	@GetMapping(path="/{badgeId}")
	public ResponseEntity<?> getBadgeById(
		@PathVariable("badgeId") Long badgeId)
	{
		BadgeResponse response = badgeService.getBadgeById(badgeId);

		return ResponseDto.ok(response);
	}

	@PostMapping
	public ResponseEntity<?> createBadge(
		@RequestBody @Valid BadgeRequest badgeRequest)
	{
		var response = badgeService.createBadge(badgeRequest);
		return ResponseDto.created(response);
	}

	@PutMapping(path="/{badgeId}")
	public ResponseEntity<?> updateBadge(
		@PathVariable("badgeId") Long badgeId,
		@RequestBody @Valid BadgeRequest badgeRequest)
	{
		var response = badgeService.updateBadge(badgeId, badgeRequest);
		return ResponseDto.ok(response);
	}

	@DeleteMapping(path="/{badgeId}")
	public ResponseEntity<?> deleteBadge(
		@PathVariable("badgeId") Long badgeId)
	{
		var response = badgeService.deleteBadge(badgeId);
		return ResponseDto.ok(response);
	}
}
