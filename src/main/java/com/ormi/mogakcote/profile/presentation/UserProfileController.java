package com.ormi.mogakcote.profile.presentation;

import com.ormi.mogakcote.post.domain.Post;
import com.ormi.mogakcote.profile.application.UserProfileService;
import com.ormi.mogakcote.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserProfileController {

    private UserProfileService userProfileService;

    @GetMapping("/api/v1/profile/{nickname}")
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
}