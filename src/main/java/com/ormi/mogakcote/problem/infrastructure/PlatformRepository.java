package com.ormi.mogakcote.problem.infrastructure;

import com.ormi.mogakcote.problem.domain.Platform;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlatformRepository extends JpaRepository<Platform, Long> {
    Optional<Platform> findByPlatformId(Long platformId);

    void deleteByPlatformId(Long platformId);
}
