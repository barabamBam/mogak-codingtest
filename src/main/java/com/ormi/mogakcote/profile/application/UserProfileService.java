package com.ormi.mogakcote.profile.application;


import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public User getUserProfile(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public List<Post> getUserPosts(User user) {
        return postRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public long getTotalPostCount(User user) {
        return postRepository.countByUserId(user.getId());
    }


    public List<Post> getTopLikedPosts(User user) {
        return postRepository.findTop3ByUserIdOrderByViewsDesc(user.getId(), PageRequest.of(0, 3));
    }
    }
