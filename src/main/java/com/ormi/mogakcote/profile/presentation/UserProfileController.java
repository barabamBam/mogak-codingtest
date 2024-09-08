package com.ormi.mogakcote.profile.presentation;

import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.profile.application.UserProfileService;
import com.ormi.mogakcote.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
public class UserProfileController {

    private UserProfileService userProfileService;

    @GetMapping("/{nickname}")
    public ResponseEntity<?> getUserProfile(@PathVariable String nickname) {
        User user = userProfileService.getUserProfile(nickname);
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(404).body(error);
        }

        List<Post> userPosts = userProfileService.getUserPosts(user);
        long totalPosts = userProfileService.getTotalPostCount(user);
        List<Post> topLikedPosts = userProfileService.getTopLikedPosts(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("posts", userPosts);
        response.put("totalPosts", totalPosts);
        response.put("topLikedPosts", topLikedPosts);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/edit")
    public ResponseEntity<Map<String, Object>> updateProfile(@RequestBody Map<String, String> updateData) {
        try {
            String nickname = updateData.get("nickname");
            String name = updateData.get("name");
            String email = updateData.get("email");
            String password = updateData.get("password");

            User updatedUser = userProfileService.updateProfile(nickname, name, email, password);

            Map<String, Object> response = new HashMap<>();
            response.put("user", updatedUser);
            response.put("message", "프로필이 성공적으로 업데이트되었습니다.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }
    }

    @DeleteMapping("/{nickname}")
    public ResponseEntity<Map<String, Object>> deleteAccount(@PathVariable String nickname) {
        try {
            userProfileService.deleteAccount(nickname);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "계정이 성공적으로 삭제되었습니다.");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(400).body(error);
        }

    }
    @GetMapping("/edit/{nickname}")
    public ResponseEntity<?> getEditProfilePage(@PathVariable String nickname) {
        User user = userProfileService.getUserProfile(nickname);
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "User not found");
            return ResponseEntity.status(404).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        return ResponseEntity.ok(response);
    }
}