package com.ormi.mogakcote.notice.application;

import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.exception.auth.AuthInvalidException;
import com.ormi.mogakcote.exception.dto.ErrorType;
import com.ormi.mogakcote.exception.notice.NoticeInvalidException;
import com.ormi.mogakcote.notice.domain.Notice;
import com.ormi.mogakcote.notice.dto.request.NoticeRequest;
import com.ormi.mogakcote.notice.dto.request.NoticeUpdateRequest;
import com.ormi.mogakcote.notice.dto.response.NoticeResponse;
import com.ormi.mogakcote.notice.infrastructure.NoticeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    /**
     * 공지사항 생성
     */
    @Transactional
    public NoticeResponse createNotice(
            Long adminId, NoticeRequest request
    ) {
        Notice notice = buildNotice(request, adminId);
        Notice savedNotice = noticeRepository.save(notice);
        return NoticeResponse.toResponse(
                savedNotice.getNoticeId(),
                savedNotice.getTitle(),
                savedNotice.getContent(),
                savedNotice.getCreatedAt(),
                savedNotice.getModifiedAt(),
                savedNotice.getAdminId()
        );
    }

    /**
     * 공지사항 조회
     */
    @Transactional(readOnly = true)
    public NoticeResponse getNotice(Long noticeId) {
        throwsIfNoticeNotExist(noticeId);
        Notice findNotice = getNoticeById(noticeId);

        return NoticeResponse.toResponse(
                findNotice.getNoticeId(),
                findNotice.getTitle(),
                findNotice.getContent(),
                findNotice.getCreatedAt(),
                findNotice.getModifiedAt(),
                findNotice.getAdminId()
        );
    }

    /**
     * 공지사항 수정
     */
    @Transactional
    public NoticeResponse updateNotice(Long noticeId, @Valid NoticeUpdateRequest updateRequest) {
        throwsIfNoticeNotExist(noticeId);
        Notice findNotice = getNoticeById(noticeId);

//        validateSameUser(findNotice.getAdminId(), adminId);

        findNotice.update(updateRequest.getTitle(), updateRequest.getContent());

        return NoticeResponse.toResponse(
                findNotice.getNoticeId(),
                findNotice.getTitle(),
                findNotice.getContent(),
                findNotice.getCreatedAt(),
                findNotice.getModifiedAt(),
                findNotice.getAdminId()
        );
    }

    /**
     * 공지사항 삭제
     */
    @Transactional
    public SuccessResponse deleteNotice (
            Long noticeId
    ) {
        throwsIfNoticeNotExist(noticeId);

        Notice findNotice = getNoticeById(noticeId);

//        validateSameUser(findNotice.getAdminId(), user.getId());

        noticeRepository.deleteById(findNotice.getNoticeId());

        return new SuccessResponse("공지사항 삭제를 성공했습니다.");
    }

    /**
     * 공지사항 전체 목록
     */
    @Transactional(readOnly = true)
    public List<NoticeResponse> getNoticeList() {
        List<NoticeResponse> noticeResponses = new ArrayList<>();
        List<Notice> findNotices = noticeRepository.findTop5ByOrderByCreatedAtDesc();

        findNotices.forEach(findNotice -> {
            noticeResponses.add(NoticeResponse.toResponse(
                    findNotice.getNoticeId(),
                    findNotice.getTitle(),
                    findNotice.getContent(),
                    findNotice.getCreatedAt(),
                    findNotice.getModifiedAt(),
                    findNotice.getAdminId()
            ));
        });

        return noticeResponses;
    }

    private static Notice buildNotice(NoticeRequest request, Long adminId) {
        return Notice.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .adminId(adminId)
                .build();
    }

    private Notice getNoticeById(Long noticeId) {
        return noticeRepository.findByNoticeId(noticeId).orElseThrow(
                () -> new NoticeInvalidException(ErrorType.NOTICE_NOT_FOUND_ERROR)
        );
    }

    private static void validateSameUser(Long noticeId, Long userId) {
        if (!noticeId.equals(userId)) {
            throw new AuthInvalidException(ErrorType.NON_IDENTICAL_USER_ERROR);
        }
    }

    private void throwsIfNoticeNotExist(Long noticeId){
        noticeRepository.findByNoticeId(noticeId).orElseThrow(
                () -> new NoticeInvalidException(ErrorType.NOTICE_NOT_FOUND_ERROR));
    }

}