package com.ormi.mogakcote.post.application;

import com.ormi.mogakcote.exception.problem.AlgorithmInvalidException;
import com.ormi.mogakcote.exception.problem.LanguageInvalidException;
import com.ormi.mogakcote.exception.problem.PlatformInvalidException;
import com.ormi.mogakcote.exception.user.UserInvalidException;
import com.ormi.mogakcote.post.dto.response.PostResponseWithNickname;
import com.ormi.mogakcote.problem.application.AlgorithmService;
import com.ormi.mogakcote.problem.application.LanguageService;
import com.ormi.mogakcote.problem.application.PlatformService;
import com.ormi.mogakcote.problem.domain.Algorithm;
import com.ormi.mogakcote.problem.domain.Language;
import com.ormi.mogakcote.problem.domain.Platform;
import com.ormi.mogakcote.problem.dto.response.AlgorithmResponse;
import com.ormi.mogakcote.problem.dto.response.LanguageResponse;
import com.ormi.mogakcote.problem.dto.response.PlatformResponse;
import com.ormi.mogakcote.problem.infrastructure.AlgorithmRepository;
import com.ormi.mogakcote.problem.infrastructure.LanguageRepository;
import com.ormi.mogakcote.problem.infrastructure.PlatformRepository;
import com.ormi.mogakcote.user.domain.User;
import com.ormi.mogakcote.user.infrastructure.UserRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.badge.application.UserBadgeService;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.auth.AuthInvalidException;
import com.ormi.mogakcote.exception.post.PostInvalidException;
import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.domain.PostFlag;
import com.ormi.mogakcote.post.domain.ReportFlag;
import com.ormi.mogakcote.post.dto.request.PostRequest;
import com.ormi.mogakcote.post.dto.request.PostSearchRequest;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.post.dto.response.PostSearchResponse;
import com.ormi.mogakcote.post.infrastructure.PostRepository;

import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.problem.domain.PostAlgorithm;
import com.ormi.mogakcote.problem.infrastructure.PostAlgorithmRepository;
import com.ormi.mogakcote.user.application.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

  private final PostRepository postRepository;
  private final PostAlgorithmRepository postAlgorithmRepository;
  private final UserService userService;
  private final UserBadgeService userBadgeService;
  private final UserRepository userRepository;
  private final AlgorithmRepository algorithmRepository;
  private final LanguageRepository languageRepository;
  private final PlatformRepository platformRepository;

  @Transactional
  public PostResponse createPost(AuthUser user, PostRequest request) {
    Post savedPost = buildAndSavePost(user.getId(), request);

    Long algorithmId = savePostAlgorithm(savedPost.getId(), request.getAlgorithmId());

    // 작성자가 해당 게시글 작성일자 하루 전 날 작성한 게시글이 있는지 확인
    boolean postExists =
        postRepository.existsPostByCreatedAt(
            LocalDateTime.of(
                LocalDate.now(ZoneId.of("Asia/Seoul")).minusDays(1), LocalTime.of(0, 0)));
    if (postExists) { // 전날 게시글을 작성하고 오늘도 작성해야 작동
      userService.updateActivity(
          user.getId(),
          "increaseDay",
          postRepository.findFirstOrderByCreatedAtDesc(),
          LocalDateTime.now());
    } else {
      userService.updateActivity(user.getId(), "resetDay");
    }
    userBadgeService.makeUserBadge(user, "POST");


    return PostResponse.toResponse(
        savedPost.getId(),
        savedPost.getTitle(),
        savedPost.getContent(),
        savedPost.getPlatformId(),
        savedPost.getProblemNumber(),
        algorithmId,
        savedPost.getLanguageId(),
        savedPost.getCode(),
        savedPost.getPostFlag().isPublic(),
        savedPost.getReportFlag().isReportRequested(),
        savedPost.getViewCnt(),
        savedPost.getVoteCnt(),
        savedPost.getPostFlag().isBanned());
  }

  @Transactional
  public PostResponseWithNickname getPost(AuthUser user, Long postId) {
    Post post = getPostById(postId);
    Long algorithmId = getAlgorithmId(postId);

    User findUser = getUserOrThrowIfNotExist(user);

    post.incrementViewCount();
    postRepository.save(post);

    return PostResponseWithNickname.toResponse(
        post.getId(),
        findUser.getNickname(),
        post.getTitle(),
        post.getContent(),
        getPlatformOrThrowIfNotExist(post.getPlatformId()).getName(),
        post.getProblemNumber(),
        getAlgorithmOrThrowIfNotExist(algorithmId).getName(),
        getLanguageOrThrowIfNotExist(post.getLanguageId()).getName(),
        post.getCode(),
        post.getPostFlag().isPublic(),
        post.getReportFlag().isReportRequested(),
        post.getViewCnt(),
        post.getVoteCnt(),
        post.getPostFlag().isBanned(),
        post.getCreatedAt(),
        post.getModifiedAt()
    );
  }

  @Transactional(readOnly = true)
  public List<PostResponse> getAllPosts() {
    List<Post> posts = postRepository.findAll();
    return posts.stream()
        .map(post ->
            PostResponse.toResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getPlatformId(),
                post.getProblemNumber(),
                getAlgorithmId(post.getId()),
                post.getLanguageId(),
                post.getCode(),
                post.getPostFlag().isPublic(),
                post.getReportFlag().isReportRequested(),
                post.getViewCnt(),
                post.getVoteCnt(),
                post.getPostFlag().isBanned()))
        .collect(Collectors.toList());
  }

  @Transactional
  public PostResponse updatePost(AuthUser user, Long postId, PostRequest request) {
    Post post = getPostById(postId);
    validateSameUser(post.getUserId(), user.getId());

    post.update(
        request.getTitle(),
        request.getContent(),
        request.getPlatformId(),
        request.getLanguageId(),
        request.getProblemNumber());

    Post updatedPost = postRepository.save(post);

    Long algorithmId = updatePostAlgorithm(postId, request.getAlgorithmId());

    return PostResponse.toResponse(
        updatedPost.getId(),
        updatedPost.getTitle(),
        updatedPost.getContent(),
        updatedPost.getPlatformId(),
        updatedPost.getProblemNumber(),
        algorithmId,
        updatedPost.getLanguageId(),
        updatedPost.getCode(),
        updatedPost.getPostFlag().isPublic(),
        updatedPost.getReportFlag().isReportRequested(),
        updatedPost.getViewCnt(),
        updatedPost.getVoteCnt(),
        updatedPost.getPostFlag().isBanned());
  }

  @Transactional
  public SuccessResponse deletePost(AuthUser user, Long postId) {
    Post post = getPostById(postId);

    validateSameUser(post.getUserId(), user.getId());

    postAlgorithmRepository.deleteByPostId(postId);
    postRepository.deleteById(postId);

    userService.updateActivity(user.getId(), "decreaseDay", post.getCreatedAt());

    return new SuccessResponse("게시글 삭제 성공");
  }

  private Post buildAndSavePost(Long userId, PostRequest request) {
    log.info("userId = {}", userId);
    PostFlag postFlag =
        PostFlag.builder().isPublic(request.isPublic()).isSuccess(false).isBanned(false)
            .build();
    ReportFlag reportFlag =
        ReportFlag.builder()
            .isReportRequested(request.isReportRequested())
            .hasPreviousReportRequested(false)
            .build();
    Post post =
        Post.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .platformId(request.getPlatformId())
            .problemNumber(request.getProblemNumber())
            .languageId(request.getLanguageId())
            .code(request.getCode())
            .postFlag(postFlag)
            .reportFlag(reportFlag)
            .viewCnt(0)
            .voteCnt(0)
            .userId(userId)
            .build();
    return postRepository.save(post);
  }

  private Post getPostById(Long postId) {
    return postRepository
        .findById(postId)
        .orElseThrow(() -> new PostInvalidException(ErrorType.POST_NOT_FOUND_ERROR));
  }

  private static void validateSameUser(Long postUserId, Long userId) {
    if (!postUserId.equals(userId)) {
      throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
    }
  }

  private Long savePostAlgorithm(Long postId, Long algorithmId) {
    PostAlgorithm postAlgorithm =
        PostAlgorithm.builder().postId(postId).algorithmId(algorithmId).build();
    return postAlgorithmRepository.save(postAlgorithm).getAlgorithmId();
  }

  private Long getAlgorithmId(Long postId) {
    return postAlgorithmRepository.findByPostId(postId).getAlgorithmId();
  }

  private Long updatePostAlgorithm(Long postId, Long newAlgorithmId) {
    postAlgorithmRepository.deleteByPostId(postId);
    return savePostAlgorithm(postId, newAlgorithmId);
  }

  // 검색 조건에 맞게 게시글 추출
  @Transactional(readOnly = true)
  public Page<PostSearchResponse> searchPost(AuthUser user, PostSearchRequest postSearchRequest) {
    // 페이징을 위한 기본 설정 -> (보여줄 페이지, 한 페이지에 보여줄 데이터 수)
    Pageable pageable = PageRequest.of(postSearchRequest.getPage() - 1, 8);

    // 검색 및 정렬 기능 수행 후 설정된 pageable에 맞게 페이지 반환
    return postRepository.searchPosts(user, postSearchRequest, pageable);
  }

  @Transactional
  public PostResponse convertBanned(Long id) {
    Post findPost = getPostById(id);

    if (findPost.getPostFlag().isBanned()) {
      findPost.updateBanned(false);
    } else {
      findPost.updateBanned(true);
    }

    return PostResponse.toResponse(
        findPost.getId(),
        findPost.getTitle(),
        findPost.getContent(),
        findPost.getPlatformId(),
        findPost.getProblemNumber(),
        getAlgorithmId(id),
        findPost.getLanguageId(),
        findPost.getCode(),
        findPost.getPostFlag().isPublic(),
        findPost.getReportFlag().isReportRequested(),
        findPost.getViewCnt(),
        findPost.getVoteCnt(),
        findPost.getPostFlag().isBanned());
  }

  private User getUserOrThrowIfNotExist(AuthUser user) {
    return userRepository.findById(user.getId()).orElseThrow(
        () -> new UserInvalidException(ErrorType.USER_NOT_FOUND_ERROR)
    );
  }

  private Algorithm getAlgorithmOrThrowIfNotExist(Long id) {
    return  algorithmRepository.findById(id).orElseThrow(
        () -> new AlgorithmInvalidException(ErrorType.ALGORITHM_NOT_FOUND_ERROR)
    );
  }

  private Language getLanguageOrThrowIfNotExist(Long languageId) {
    return languageRepository.findById(languageId).orElseThrow(
        () -> new LanguageInvalidException(ErrorType.LANGUAGE_NOT_FOUND_ERROR)
    );
  }

  private Platform getPlatformOrThrowIfNotExist(Long platformId){
    return platformRepository.findById(platformId).orElseThrow(
        () -> new PlatformInvalidException(ErrorType.PLATFORM_NOT_FOUND_ERROR)
    );
  }
}