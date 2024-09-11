package com.ormi.mogakcote.profile.presentation;

import static com.ormi.mogakcote.common.CrossOriginConstants.CROSS_ORIGIN_ADDRESS;

import com.ormi.mogakcote.auth.model.AuthUser;
import com.ormi.mogakcote.common.dto.SuccessResponse;
import com.ormi.mogakcote.common.model.ResponseDto;
import com.ormi.mogakcote.profile.application.UserProfileService;
import com.ormi.mogakcote.profile.dto.request.UserProfileUpdateRequest;
import com.ormi.mogakcote.profile.dto.response.PostCntResponse;
import com.ormi.mogakcote.profile.dto.response.PostProfileInfoResponse;
import com.ormi.mogakcote.profile.dto.response.UserProfileInfoResponse;
import com.ormi.mogakcote.user.dto.response.UserResponse;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = CROSS_ORIGIN_ADDRESS)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class UserProfileController {

    private final UserProfileService userProfileService;

    /**
     * 사용자의 정보 조회
     */
    @GetMapping
    public ResponseEntity<?> getUserProfile(
            AuthUser user
    ) {
        UserResponse response = userProfileService.getUserProfile(
                user);
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자의 게시글 목록 조회
     */
    @GetMapping("/posts")
    public ResponseEntity<?> getUserPosts(
            AuthUser user,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "3") int size
    ) {
        List<PostProfileInfoResponse> response = userProfileService.getUserPosts(user, page, size);
        return ResponseDto.ok(response);
    }

    /**
     * 사용자의 게시글 개수 조회
     */
    @GetMapping("/post-cnt")
    public ResponseEntity<?> getUserPostCnt(
            AuthUser user
    ) {
        PostCntResponse response = userProfileService.getUserPostCnt(
                user);
        return ResponseDto.ok(response);
    }

    /**
     * 사용자의 Top3 목록 조회
     */
    @GetMapping("/top-3-posts")
    public ResponseEntity<?> getUserTop3LikedPosts(
            AuthUser user
    ) {
        List<PostProfileInfoResponse> response = userProfileService.getUserTop3LikedPosts(user);
        return ResponseDto.ok(response);
    }

    /**
     * 사용자 프로필 수정
     */
    @PostMapping("/edit")
    public ResponseEntity<?> getEditProfilePage(
            AuthUser user, @RequestBody UserProfileUpdateRequest request
    ) {
        UserProfileInfoResponse response = userProfileService.updateProfile(
                user, request);
        return ResponseDto.ok(response);
    }

    /**
     * 사용자 계정 삭제(탈퇴)
     */
    @DeleteMapping
    public ResponseEntity<?> deleteAccount(
            AuthUser user
    ) {
        SuccessResponse response = userProfileService.deleteAccount(user);
        return ResponseDto.ok(response);
    }
}