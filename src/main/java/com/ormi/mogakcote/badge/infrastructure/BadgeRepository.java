package com.ormi.mogakcote.badge.infrastructure;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ormi.mogakcote.badge.domain.Badge;

public interface BadgeRepository extends JpaRepository<Badge, Long> {

	@Query("select b from Badge b where b.name = ?1")
	Optional<Badge> findByName(String name);
}
