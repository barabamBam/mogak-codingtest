package com.ormi.mogakcote.badge.application;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ormi.mogakcote.badge.domain.Badge;
import com.ormi.mogakcote.badge.dto.request.BadgeRequest;
import com.ormi.mogakcote.badge.dto.response.BadgeResponse;
import com.ormi.mogakcote.badge.infrastructure.BadgeRepository;
import com.ormi.mogakcote.badge.infrastructure.UserBadgeRepository;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.badge.BadgeInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BadgeService {

  private final BadgeRepository badgeRepository;
  private final UserBadgeRepository userBadgeRepository;

  @Transactional(readOnly = true)
  public List<BadgeResponse> getBadges() {
    return badgeRepository.findAll().stream()
            .map(BadgeService::getBadgeResponse)
            .toList();
  }

  @Transactional(readOnly = true)
  public BadgeResponse getBadgeById(Long badgeId) {
     Badge badge = findBadgeById(badgeId);
     return getBadgeResponse(badge);
  }

  @Transactional
  public BadgeResponse createBadge(BadgeRequest badgeRequest) {
    Badge badge =
        Badge.builder()
            .name(badgeRequest.getName())
            .description(badgeRequest.getDescription())
            .build();
    Badge savedBadge = badgeRepository.save(badge);

    return getBadgeResponse(savedBadge);
  }

  @Transactional
  public BadgeResponse updateBadge(Long badgeId, BadgeRequest badgeRequest) {
    Badge findBadge = findBadgeById(badgeId);

    findBadge.update(
        badgeRequest.getName(),
        badgeRequest.getDescription()
    );

    Badge updatedBadge = badgeRepository.save(findBadge);

    return getBadgeResponse(updatedBadge);
  }

  // 해당 뱃지가 사라지면 userBadge도 같이 삭제됨
  @Transactional
  public SuccessResponse deleteBadge(Long badgeId) {
    badgeRepository.delete(findBadgeById(badgeId));
    userBadgeRepository.deleteAll(userBadgeRepository.findAllByBadgeId(badgeId));

    return new SuccessResponse("뱃지가 성공적으로 삭제되었습니다.");
  }

  // id에 해당하는 뱃지 찾기
  private Badge findBadgeById(Long badgeId) {
    return badgeRepository.findById(badgeId)
        .orElseThrow(() -> new BadgeInvalidException(ErrorType.BADGE_NOT_FOUND_ERROR));
  }

  // Badge 객체를 BadgeResponse 객체로 바꿔줌
  private static BadgeResponse getBadgeResponse(Badge badge) {
    return BadgeResponse.toResponse(
        badge.getId(),
        badge.getName(),
        badge.getDescription()
    );
  }

}
