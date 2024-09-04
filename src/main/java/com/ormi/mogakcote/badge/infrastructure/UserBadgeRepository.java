package com.ormi.mogakcote.badge.infrastructure;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ormi.mogakcote.badge.domain.UserBadge;

@Repository
@Transactional(readOnly = true)
public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {

	List<UserBadge> findAllByUserId(Long userId);
	List<UserBadge> findAllByBadgeId(Long BadgeId);
}
