package com.ormi.mogakcote.post_activity.application;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.badge.application.UserBadgeService;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.post.domain.Post;

import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.post_activity.domain.Vote;
import com.ormi.mogakcote.post_activity.dto.response.VoteResponse;
import com.ormi.mogakcote.post_activity.infrastructure.VoteRepository;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;
    private final UserBadgeService userBadgeService;
    private final UserRepository userRepository;

    @Transactional
    public VoteResponse clickVote(AuthUser user, Long postId) {
        // Post 가 존재하는지 확인
        Post post = checkExistsPost(postId);

        // user 가 존재하는지 확인
        User findUser = getUserOrThrowIfNotExist(user);

        // 이전 vote 여부에 따라 vote 추가/취소
        if (!voteRepository.existsByUserIdAndPostId(findUser.getId(), postId)) {
            buildAndSaveVote(postId, findUser);
            post.incrementVoteCount();
        } else {
            voteRepository.deleteByUserIdAndPostId(findUser.getId(), postId);
            post.decrementVoteCount();
        }

        userBadgeService.makeUserBadge(user, "VOTE");

        return VoteResponse.toResponse(post.getVoteCnt());
    }

    @Transactional(readOnly = true)
    public List<Vote> getVotesByUserId(Long userId) {
        return voteRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Vote> getVotesByPostId(Long postId) {
        return voteRepository.findByPostId(postId);
    }

    @Transactional(readOnly = true)
    public Optional<Vote> getVote(Long id) {
        return voteRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Post getPostForVote(Vote vote) {
        return checkExistsPost(vote.getPostId());
    }

    @Transactional
    public void deleteVote(Long id, Long postId) {
        // 먼저 Post가 존재하는지 확인
        Post post = checkExistsPost(postId);

        voteRepository.deleteById(id);

        post.decrementVoteCount();
        postRepository.save(post);

    }

    @Transactional(readOnly = true)
    public long countVotesByPostId(Long postId) {
        return voteRepository.countByPostId(postId);
    }

    private User getUserOrThrowIfNotExist(AuthUser user) {
        return userRepository.findById(user.getId()).orElseThrow(
                () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
        );
    }

    private Post checkExistsPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    private void buildAndSaveVote(Long postId, User findUser) {
        Vote vote = Vote.builder()
                .postId(postId)
                .userId(findUser.getId())
                .build();
        voteRepository.save(vote);
    }
}