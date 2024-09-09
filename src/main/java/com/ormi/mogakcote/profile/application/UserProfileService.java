package com.ormi.mogakcote.profile.application;


import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public List<Post> getTopLikedPosts(User user) {
        return postRepository.findTop3ByUserIdOrderByViewsDesc(user.getId(), PageRequest.of(0, 3));
    }

    @Transactional
    public User updateProfile(String nickname, String name, String email, String password) {
        User existingUser = userRepository.findByNickname(nickname);
        if (existingUser == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        existingUser.updateProfile(name, nickname, email);

        if (password != null && !password.isEmpty()) {
            existingUser.updatePassword(password);
        }

        return userRepository.save(existingUser);
    }

    @Transactional
    public void deleteAccount(String nickname) {
        User user = userRepository.findByNickname(nickname);
        if (user == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }
        userRepository.delete(user);
    }
}
