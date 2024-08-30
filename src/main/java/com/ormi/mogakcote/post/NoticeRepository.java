package com.ormi.mogakcote.post;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface NoticeRepository extends JpaRepository<Notice, Long> {

	@Query("SELECT n FROM Notice n ORDER BY n.createdAt DESC LIMIT 5")
	List<Notice> getNoticeLatestFive();
}
