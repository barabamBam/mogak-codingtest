package com.ormi.mogakcote.profile.application;


import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.profile.dto.request.UserProfileUpdateRequest;
import com.ormi.mogakcote.profile.dto.response.PostCntResponse;
import com.ormi.mogakcote.profile.dto.response.PostProfileInfoResponse;
import com.ormi.mogakcote.profile.dto.response.UserProfileInfoResponse;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.dto.response.UserResponse;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(AuthUser user) {

        User findUser = getUserOrThrowIfNotExist(user);

        return UserResponse.toResponse(
                findUser.getId(), findUser.getName(), findUser.getNickname(), findUser.getEmail(),
                findUser.getPassword()
        );
    }

    @Transactional(readOnly = true)
    public List<PostProfileInfoResponse> getUserPosts(AuthUser user, int page, int size) {
        User findUser = getUserOrThrowIfNotExist(user);
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Post> findPosts = postRepository.findByUserIdOrderByCreatedAtDesc(findUser.getId(), pageRequest);

        return findPosts.stream()
                .map(post -> PostProfileInfoResponse.toResponse(
                        post.getId(), post.getTitle(), post.getContent(), post.getCreatedAt(),
                        post.getViewCnt()
                ))
                .collect(Collectors.toList());
    }
//    public List<PostProfileInfoResponse> getUserPosts(AuthUser user) {
//        User findUser = getUserOrThrowIfNotExist(user);
//
//        List<PostProfileInfoResponse> responses = new ArrayList<>();
//
//        List<Post> findPosts = postRepository.findByUserIdOrderByCreatedAtDesc(
//                findUser.getId(), PageRequest.of(0, 3));
//
//        findPosts.forEach(post -> {
//            responses.add(PostProfileInfoResponse.toResponse(
//                    post.getId(), post.getTitle(), post.getContent(), post.getCreatedAt(),
//                    post.getViewCnt()
//            ));
//        });
//
//        return responses;
//    }

    @Transactional(readOnly = true)
    public PostCntResponse getUserPostCnt(AuthUser user) {
        User findUser = getUserOrThrowIfNotExist(user);
        long postCnt = postRepository.countByUserId(findUser.getId());

        return PostCntResponse.toResponse(postCnt);
    }

    @Transactional(readOnly = true)
    public List<PostProfileInfoResponse> getUserTop3LikedPosts(AuthUser user) {
        List<Post> top3Posts = postRepository.findTop3ByUserIdOrderByViewsDesc(
                user.getId(), PageRequest.of(0, 3));

        List<PostProfileInfoResponse> postProfileInfoResponses = new ArrayList<>();
        top3Posts.forEach(post -> {
            postProfileInfoResponses.add(
                    PostProfileInfoResponse.toResponse(post.getId(), post.getTitle(),post.getContent(), post.getCreatedAt(), post.getViewCnt())
            );
        });

        return postProfileInfoResponses;
    }

    @Transactional
    public UserProfileInfoResponse updateProfile(AuthUser user, UserProfileUpdateRequest request) {
        User findUser = getUserOrThrowIfNotExist(user);
        findUser.updateProfile(request.getUsername(), request.getNickname(), request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            findUser.updatePassword(request.getPassword());
        }

        User savedUser = userRepository.save(findUser);

        return UserProfileInfoResponse.toResponse(savedUser.getName(), savedUser.getNickname(),
                savedUser.getEmail(), savedUser.getPassword());
    }

    @Transactional
    public SuccessResponse deleteAccount(AuthUser user) {
        User findUser = getUserOrThrowIfNotExist(user);
        userRepository.deleteById(findUser.getId());

        return new SuccessResponse("회원 탈퇴 성공");
    }

    private User getUserOrThrowIfNotExist(AuthUser user) {
        return userRepository.findById(user.getId()).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        );
    }

}
