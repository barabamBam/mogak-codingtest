package com.ormi.mogakcote.post.infrastructure;

import static com.ormi.mogakcote.post.domain.QPost.*;
import static com.ormi.mogakcote.post.dto.request.SortType.*;
import static com.ormi.mogakcote.problem.domain.QAlgorithm.*;
import static com.ormi.mogakcote.problem.domain.QLanguage.*;
import static com.ormi.mogakcote.problem.domain.QPostAlgorithm.*;
import static com.ormi.mogakcote.problem.domain.QProblemReportAlgorithm.*;
import static com.ormi.mogakcote.vote.domain.QVote.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.post.domain.QPost;
import com.ormi.mogakcote.post.dto.request.PostSearchRequest;
import com.ormi.mogakcote.post.dto.request.SortType;
import com.ormi.mogakcote.post.dto.response.PostSearchResponse;
import com.ormi.mogakcote.problem.infrastructure.AlgorithmRepository;
import com.ormi.mogakcote.problem.infrastructure.LanguageRepository;
import com.ormi.mogakcote.problem.infrastructure.PostAlgorithmRepository;
import com.ormi.mogakcote.problem.infrastructure.ProblemReportAlgorithmRepository;
import com.ormi.mogakcote.vote.domain.QVote;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

// PostRepositoryCustom의 구현체로 PostRepository에서 인터페이스를 확장시켜 사용
// 따라서 해당 구현체 내 오버라이딩 된 메서드 또한 사용 가능해짐
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom {

  private final LanguageRepository languageRepository;
  private final AlgorithmRepository algorithmRepository;
  private final ProblemReportAlgorithmRepository reportAlgorithmRepository;
  private final PostAlgorithmRepository postAlgorithmRepository;

  private final JPAQueryFactory jpaQueryFactory;

  // 검색 조건에 일치하는 게시글을 불러오기 위한 로직
  @Override
  public Page<PostSearchResponse> searchPosts(
      AuthUser user, PostSearchRequest postSearchRequest, Pageable pageable) {

    // import com.ormi.mogakcote.problem.infrastructure.ProblemReportAlgorithmRepository; 추가
    // Post 도메인 probReportId 추가
    JPAQuery<Post> query =
        jpaQueryFactory
            .selectFrom(post)
            .leftJoin(vote)
            .on(vote.postId.eq(post.id)) // 게시글의 추천 수 를 확인하기 위해 join
            .where(
                // 공개 게시글과 자신의 비공개 게시글만 추출
                (post.postFlag
                        .isPublic
                        .eq(true)
                        .or(user != null ? post.userId.eq(user.getId()) : null))
                    // 제목 및 내용에 키워드가 포함하고 있는지 확인
                    .and(
                        StringUtils.hasText(postSearchRequest.getKeyword())
                            ? (containWord(postSearchRequest.getKeyword(), post.title)
                                .or(containWord(postSearchRequest.getKeyword(), post.content)))
                            : null)
                    // 선택한 알고리즘을 포함하고 있는지 확인
                    .and(
                        StringUtils.hasText(postSearchRequest.getAlgorithm())
                            ? (isEqual(
                                    postSearchRequest.getAlgorithm(),
									getAlgorithm("postAlgorithm"))
                                .or(
                                    isEqual(
                                        postSearchRequest.getAlgorithm(),
										getAlgorithm("problemReportAlgorithm"))))
                            : null
					)
                    // 선택한 언어로 된 코드인지 확인
                    .and(
						StringUtils.hasText(postSearchRequest.getLanguage())
							? (isEqual(
                            postSearchRequest.getLanguage(),
							  (jpaQueryFactory
								  .select(language.languageName)
								  .from(post)
								  .leftJoin(language)
								  .on(language.id.eq(post.languageId))
								  .fetchOne()))
						) : null
					)
                    // 코드 성공 여부 확인
                    .and(
                        postSearchRequest.isCheckSuccess()
                            ? post.postFlag.isSuccess.eq(true)
                            : null))
            .groupBy(post.id)
            // 정렬 키워드에 맞게 정렬하고, 같은 값이 있으면 최신순으로 정렬
            .orderBy(
                getSortByResult(postSearchRequest.getSortBy(), post, vote), post.createdAt.desc());

    // 출력될 쿼리 결과가 총 몇 개인지 확인 -> 게시물 개수 확인 해서 페이징
    long totalCount = query.fetch().size();
    System.out.println("count: " + totalCount);

    query =
        query
            .offset(pageable.getOffset()) // pageable에 설정된 값으로 몇번째 데이터 부터 보여줄 지가 정의됨
            .limit(pageable.getPageSize()); // offset 부터 몇 개의 데이터를 출력될 지가 정의됨

    // 쿼리 실행이 끝난 데이터를 리스트 형태로 받은 후 Page<>에 담아 반환
    return new PageImpl<>(
        query.fetch().stream()
            .map(
                post ->
                    PostSearchResponse.builder()
                        .id(post.getId())
                        .title(post.getTitle())
                        .content(post.getContent())
                        .algorithms(Arrays.asList(
							getCodeAlgorithm(post.getId()), getCodeReportAlgorithm(post.getProbReportId())))
                        .viewCnt(post.getViewCnt())
                        .createdAt(post.getCreatedAt())
                        .build())
            .collect(Collectors.toList()),
        pageable,
        totalCount);
  }

  private String getCodeAlgorithm(Long postId) {
    return algorithmRepository.findNameById(
        postAlgorithmRepository.findAlgorithmIdByPostId(postId));
  }

  private String getCodeReportAlgorithm(Long probReportId) {
    return algorithmRepository.findNameById(
        reportAlgorithmRepository.findAlgorithmIdByProbReportId(probReportId));
  }

  private String getAlgorithm(String pos) {
    if (pos.equals("postAlgorithm")) {
      return (jpaQueryFactory
          .select(algorithm.name)
          .from(algorithm)
          .leftJoin(postAlgorithm)
          .on(algorithm.id.eq(postAlgorithm.algorithmId))
          .where(postAlgorithm.postId.eq(post.id))
          .fetchOne());
    } else if (pos.equals("problemReportAlgorithm")) {
      return (jpaQueryFactory
          .select(algorithm.name)
          .from(algorithm)
          .leftJoin(problemReportAlgorithm)
          .on(algorithm.id.eq(problemReportAlgorithm.algorithmId))
          .where(problemReportAlgorithm.id.eq(post.probReportId))
          .fetchOne());
    }
    return null;
  }

  // 일치 확인 메서드
  private static BooleanExpression isEqual(String searchData, String tableData) {
    return Expressions.asBoolean(
        StringUtils.hasText(searchData) ? searchData.equalsIgnoreCase(tableData) : null);
  }

  // 포함 확인 메서드
  private static BooleanExpression containWord(String searchData, StringPath tableData) {
    return Expressions.stringTemplate("LOWER({0})", tableData)
        .contains(Expressions.stringTemplate("LOWER({0})", searchData));
  }

  // 사용자가 원하는 정렬 기준을 가지고 정렬
  private OrderSpecifier<?> getSortByResult(SortType sortBy, QPost post, QVote vote) {
    return switch (sortBy) {
        // 게시글을 최신 순으로 정렬
      case LATEST -> post.createdAt.desc();

        // 게시글을 오래된 순으로 정렬
      case OLDEST -> post.createdAt.asc();

        // 게시글을 조회수 많은 순으로 정렬
      case MOST_VIEWED -> post.viewCnt.desc();

        // 게시글을 추천 순으로 정렬
      case MOST_LIKED -> vote.count().desc();
    };
  }
}
