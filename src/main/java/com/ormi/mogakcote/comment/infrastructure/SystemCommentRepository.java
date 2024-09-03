package com.ormi.mogakcote.comment.infrastructure;

import com.ormi.mogakcote.comment.domain.SystemComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface SystemCommentRepository extends JpaRepository<SystemComment, Long> {

    SystemComment findByPostId(Long postId);
}
