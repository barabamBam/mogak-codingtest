package com.ormi.mogakcote.badge.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ormi.mogakcote.badge.domain.UserBadge;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

	List<UserBadge> findAllByUserId(Long userId);
	List<UserBadge> findAllByBadgeId(Long BadgeId);
}
