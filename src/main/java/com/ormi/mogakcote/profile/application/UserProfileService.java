package com.ormi.mogakcote.profile.application;


import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public User getUserProfile(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    @Transactional(readOnly = true)
    public List<Post> getUserPosts(User user) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    @Transactional(readOnly = true)
    public long getTotalPostCount(User user) {
        return postRepository.countByUserId(user.getId());
    }

    @Transactional(readOnly = true)
    public List<Post> getTopLikedPosts(User user) {
        return postRepository.findTop3ByUserIdOrderByViewsDesc(user.getId(), PageRequest.of(0, 3));
    }
    }
