package com.ormi.mogakcote.badge.application;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.badge.domain.Badge;
import com.ormi.mogakcote.badge.domain.UserBadge;
import com.ormi.mogakcote.badge.dto.BadgeType;
import com.ormi.mogakcote.badge.dto.response.UserBadgeResponse;
import com.ormi.mogakcote.badge.infrastructure.BadgeRepository;
import com.ormi.mogakcote.badge.infrastructure.UserBadgeRepository;
import com.ormi.mogakcote.exception.badge.BadgeInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserBadgeService {

  private final BadgeRepository badgeRepository;
  private final UserBadgeRepository userBadgeRepository;

  private final UserRepository userRepository;
  private final PostRepository postRepository;

  @Transactional(readOnly = true)
  public List<UserBadgeResponse> getUserBadges(AuthUser user) {
    List<UserBadge> badges = userBadgeRepository.findAllByUserId(user.getId());
    List<UserBadgeResponse> badgesResponse = new ArrayList<>();

    badges.forEach(
        badge -> {
          Badge findBadge = getBadgeById(badge.getBadgeId());
          badgesResponse.add(
              UserBadgeResponse.toResponse(
                  badge.getId(),
                  badge.getAcquiredAt(),
                  findBadge.getName(),
                  findBadge.getDescription()));
        });

    return badgesResponse;
  }

  @Transactional
  public void makeUserBadge(AuthUser user, String badgeType) {

    Integer cnt = 0;  // 뱃지에서 개수를 지정하기 위한 변수
    BadgeType satisfiedBadgeType = null;  // 뱃지의 타입을 찾기 위한 변수
    boolean checkExists = true; // 현재 할당하려는 뱃지가 존재하는지 확인을 위한 변수

    // 사용자에게 해당 뱃지를 주어도 될 지 확인하는 작업 수행
    // 이때 사용자에게 할당할 뱃지의 타입을 체크하는 작업이 같이 수행됨
    switch (badgeType) {
      case "COMMENT":
        cnt = userRepository.findCommentCountById(user.getId());
        satisfiedBadgeType = cnt >= 100
            ? BadgeType.COMMENT_100
              : (cnt >= 50 ? BadgeType.COMMENT_50
                : (cnt >= 20 ? BadgeType.COMMENT_20 : null));
        break;
      case "VOTE":
        cnt = postRepository.findVoteCountById(user.getId());
        satisfiedBadgeType = cnt >= 1000
            ? BadgeType.VOTE_1000
              : (cnt >= 500 ? BadgeType.VOTE_500
                : (cnt >= 100 ? BadgeType.VOTE_100 : null));
        break;
      case "POST":
        cnt = userRepository.findDayCountById(user.getId());
        satisfiedBadgeType = cnt >= 365
            ? BadgeType.POST_YEAR
              : (cnt >= 180 ? BadgeType.POST_HALF
              : (cnt >= 30 ? BadgeType.POST_MONTH : null));
        break;
    }

    if(satisfiedBadgeType != null)
      checkExists = existBadgeAlready(user.getId(), satisfiedBadgeType);

    System.out.println(cnt);
    System.out.println(satisfiedBadgeType);
    System.out.println(checkExists);

    // 같은 뱃지가 없고 뱃지를 줄 요건이 충족되었다면 뱃지 생성
    if (!checkExists) {
      UserBadge badgeForSave = UserBadge.builder()
          .userId(user.getId())
          .acquiredAt(LocalDateTime.now())
          .badgeId(getBadgeByName(satisfiedBadgeType.toString()).getId())
          .build();
      userBadgeRepository.save(badgeForSave);
    }
  }

  // 일치하는 뱃지를 현재 사용자가 가지고 있는지 확인을 위한 메서드
  private boolean existBadgeAlready(Long userId, BadgeType badgeType) {

    String badgeName = badgeType.toString();
    Badge findBadge = getBadgeByName(badgeName);

    for (UserBadge userBadge : userBadgeRepository.findAllByUserId(userId)) {
      if (userBadge.getBadgeId().equals(findBadge.getId())) return true;
    }

    return false;
  }

  private Badge getBadgeById(Long id) {
    return badgeRepository
        .findById(id)
        .orElseThrow(() -> new BadgeInvalidException(ErrorType.BADGE_NOT_FOUND_ERROR));
  }

  private Badge getBadgeByName(String badgeName) {
    return badgeRepository
        .findByName(badgeName)
        .orElseThrow(() -> new BadgeInvalidException(ErrorType.BADGE_NOT_FOUND_ERROR));
  }
}
