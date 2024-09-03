package com.ormi.mogakcote.profile.vote;

import com.ormi.mogakcote.post.domain.Post;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "vote")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "user_id")
    private Long userId;

    @Column(name = "post_id", nullable = false)
    private Long postId;
}