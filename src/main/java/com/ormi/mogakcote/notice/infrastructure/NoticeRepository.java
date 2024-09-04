package com.ormi.mogakcote.notice.infrastructure;

import com.ormi.mogakcote.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    Optional<Notice> findByNoticeId(Long noticeId);
    List<Notice> findTop5ByOrderByCreatedAtDesc();

    @Query("SELECT n FROM Notice n ORDER BY n.createdAt DESC LIMIT 5")
    List<Notice> getNoticeLatestFive();
}
