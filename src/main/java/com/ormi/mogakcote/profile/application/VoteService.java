package com.ormi.mogakcote.profile.application;

import com.ormi.mogakcote.post.domain.Post;

import com.ormi.mogakcote.post.infrastructure.PostRepository;
import com.ormi.mogakcote.profile.infrastructure.VoteRepository;
import com.ormi.mogakcote.profile.vote.Vote;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class VoteService {

    private final VoteRepository voteRepository;
    private final PostRepository postRepository;

    @Autowired
    public VoteService(VoteRepository voteRepository, PostRepository postRepository) {
        this.voteRepository = voteRepository;
        this.postRepository = postRepository;
    }

    @Transactional
    public Vote createVote(Long userId, Long postId) {
        // 먼저 Post가 존재하는지 확인
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));

        Vote vote = new Vote();
        vote.setUserId(userId);
        vote.setPostId(postId);

        return voteRepository.save(vote);
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
        return postRepository.findById(vote.getPostId())
            .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    @Transactional
    public void deleteVote(Long id) {
        voteRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long countVotesByPostId(Long postId) {
        return voteRepository.countByPostId(postId);
    }
}
