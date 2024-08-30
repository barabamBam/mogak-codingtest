package com.ormi.mogakcote.post;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ormi.mogakcote.auth.model.AuthUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {

	private final PostRepository postRepository;
	private final NoticeRepository noticeRepository;

	// 공지사항 최신 5개만 추출
	public List<NoticeResponse> getNoticeLatestFive() {
		List<Notice> notices = noticeRepository.getNoticeLatestFive();
		List<NoticeResponse> noticeResponses = new ArrayList<>();
		notices.forEach(notice -> noticeResponses.add(
			NoticeResponse.builder()
			.title(notice.getTitle())
			.createdAt(notice.getCreatedAt())
			.build())
		);
		return noticeResponses;
	}

	// 검색 조건에 맞게 게시글 추출
	public Page<PostResponse> searchPost(AuthUser user, PostSearchRequest postSearchRequest) {
		// 페이징을 위한 기본 설정 -> (보여줄 페이지, 한 페이지에 보여줄 데이터 수)
		Pageable pageable = PageRequest.of(postSearchRequest.getPage()-1, 8);

    	//System.out.println(postSearchRequest.getKeyword()+ postSearchRequest.getAlgorithm()+ postSearchRequest.getLanguage()+ postSearchRequest.getSortBy()+ postSearchRequest.getPage()+ pageable);
		// 검색 및 정렬 기능 수행 후 설정된 pageable에 맞게 페이지 반환
		return postRepository.searchPosts(user, postSearchRequest, pageable);
	}


}
