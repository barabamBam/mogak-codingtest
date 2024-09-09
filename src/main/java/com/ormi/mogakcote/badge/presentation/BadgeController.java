package com.ormi.mogakcote.badge.presentation;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
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
@RequestMapping(path="/api/v1/admin")
@RequiredArgsConstructor
public class BadgeController {

	private final BadgeService badgeService;

	@GetMapping(path="/badge/list")
	public String getBadges(Model model)
	{
		List<BadgeResponse> response = badgeService.getBadges();
		model.addAttribute("badges", response);

		// TODO: 관리자페이지
		return "";
	}


	@GetMapping(path="/badge/{badgeId}")
	public String getBadgeById(
		@PathVariable("badgeId") Long badgeId, Model model)
	{
		BadgeResponse response = badgeService.getBadgeById(badgeId);
		model.addAttribute("badge", response);

		getBadgeByIdResponse(response);

		// TODO: 관리자페이지
		return "";
	}

	public ResponseEntity<?> getBadgeByIdResponse(BadgeResponse response)
	{
		return ResponseDto.ok(response);
	}

	@PostMapping(path="/createBadge")
	public ResponseEntity<?> createBadge(
		@RequestBody @Valid BadgeRequest badgeRequest)
	{
		var response = badgeService.createBadge(badgeRequest);
		return ResponseDto.created(response);
	}

	@PutMapping(path="/updateBadge/{badgeId}")
	public ResponseEntity<?> updateBadge(
		@PathVariable("badgeId") Long badgeId,
		@RequestBody @Valid BadgeRequest badgeRequest)
	{
		var response = badgeService.updateBadge(badgeId, badgeRequest);
		return ResponseDto.ok(response);
	}

	@DeleteMapping(path="/deleteBadge/{badgeId}")
	public ResponseEntity<?> deleteBadge(
		@PathVariable("badgeId") Long badgeId)
	{
		var response = badgeService.deleteBadge(badgeId);
		return ResponseDto.ok(response);
	}
}
