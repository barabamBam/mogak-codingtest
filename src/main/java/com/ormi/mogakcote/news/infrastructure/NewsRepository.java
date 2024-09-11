package com.ormi.mogakcote.news.infrastructure;

import com.ormi.mogakcote.news.domain.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT n FROM News n WHERE n.receiverId = :receiverId ORDER BY n.createdAt DESC")
    Page<News> findAllNewsByReceiverId(@Param("receiverId") Long receiverId, Pageable pageable);
}

