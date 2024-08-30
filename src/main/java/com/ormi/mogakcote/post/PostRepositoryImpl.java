package com.ormi.mogakcote.post;

import static com.ormi.mogakcote.post.QPost.*;
import static com.ormi.mogakcote.post.QVote.*;

import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import com.ormi.mogakcote.auth.model.AuthUser;
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

	private final JPAQueryFactory jpaQueryFactory;
	//private final AuthUser authUser;

	// 검색 조건에 일치하는 게시글을 불러오기 위한 로직
	@Override
	public Page<PostResponse> searchPosts(AuthUser user, PostSearchRequest postSearchRequest, Pageable pageable) {

    JPAQuery<Post> query =
        jpaQueryFactory
            .selectFrom(post)
            .leftJoin(vote)
            .on(vote.postId.eq(post.id))
            .where(
                (post.isPublic.eq(true).or(post.userId.eq(user.getId())))
                    .and(StringUtils.hasText(postSearchRequest.getKeyword()) ?
                        (containWord(postSearchRequest.getKeyword(), post.title)
                        .or(containWord(postSearchRequest.getKeyword(), post.content))) : null)
                    .and(isEqual(postSearchRequest.getAlgorithm(), post.algorithm))
                    .and(isEqual(postSearchRequest.getLanguage(), post.language))
                    .and(postSearchRequest.isCheckSuccess() ? post.checkSuccess.eq(true) : null))
            .groupBy(post.id)
            .orderBy(getSortByResult(postSearchRequest.getSortBy(), post, vote), post.createdAt.desc());

		// 출력될 쿼리 결과가 총 몇 개인지 확인 -> 게시물 개수 확인 해서 페이징
		long totalCount = query.fetch().size();
    System.out.println("count: "+totalCount);

		query = query.offset(pageable.getOffset()) // pageable에 설정된 값으로 몇번째 데이터 부터 보여줄 지가 정의됨
			.limit(pageable.getPageSize()); // offset 부터 몇 개의 데이터를 출력될 지가 정의됨

		// 쿼리 실행이 끝난 데이터를 리스트 형태로 받은 후 Page<>에 담아 반환
		return new PageImpl<>(
			query.fetch().stream().map(post ->
				PostResponse.builder()
					.id(post.getId())
					.title(post.getTitle())
					.content(post.getContent())
					.algorithm(post.getAlgorithm())
					.viewCnt(post.getViewCnt())
					.createdAt(post.getCreatedAt())
					.build()
				)
				.collect(Collectors.toList()), pageable, totalCount);
	}


	// 일치 확인 메서드
	private static BooleanExpression isEqual(String searchData, StringPath tableData) {
		return StringUtils.hasText(searchData) ?
			Expressions.stringTemplate(
					"LOWER({0})", tableData)
				.eq(Expressions.stringTemplate(
					"LOWER({0})", searchData)) : null;
	}

	// 포함 확인 메서드
	private static BooleanExpression containWord(String searchData, StringPath tableData) {
		return Expressions.stringTemplate(
					"LOWER({0})", tableData)
				.contains(Expressions.stringTemplate(
					"LOWER({0})", searchData));
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

			//게시글을 추천 순으로 정렬
			case MOST_LIKED -> vote.count().desc();

		};
	}

}
