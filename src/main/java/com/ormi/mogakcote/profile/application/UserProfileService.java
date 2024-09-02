package com.ormi.mogakcote.profile.application;

import com.ormi.mogakcote.profile.vote.Post;
import com.ormi.mogakcote.profile.infrastructure.PostRepository;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private UserRepository userRepository;
    private PostRepository postRepository;

    public User getUserProfile(String nickname) {
        return userRepository.findByNickname(nickname);
    }

    public List<Post> getUserPosts(User user) {
        return postRepository.findByUserOrderByCreatedAtDesc(user);
    }

    public long getTotalPostCount(User user) {
        return postRepository.countByUser(user);
    }

    public List<Post> getTopLikedPosts(User user) {
        return postRepository.findTop3ByUserOrderByLikesDesc(user, PageRequest.of(0, 3));
    }
}