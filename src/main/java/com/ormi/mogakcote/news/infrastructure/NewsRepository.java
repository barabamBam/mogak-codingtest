package com.ormi.mogakcote.news.infrastructure;

import com.ormi.mogakcote.news.domain.News;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface NewsRepository extends JpaRepository<News, Long> {

    @Query("SELECT n FROM News n WHERE n.receiverId = :receiverId AND n.isViewed = false ORDER BY n.createdAt DESC")
    List<News> findUnviewedNewsByReceiverId(@Param("receiverId") Long receiverId);

    @Query("SELECT n FROM News n WHERE n.receiverId = :receiverId AND n.isViewed = true ORDER BY n.createdAt DESC")
    List<News> findViewedNewsByReceiverId(@Param("receiverId") Long receiverId);
}

