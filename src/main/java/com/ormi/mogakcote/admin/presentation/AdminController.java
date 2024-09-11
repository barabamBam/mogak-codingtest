package com.ormi.mogakcote.admin.presentation;

import com.ormi.mogakcote.notice.application.NoticeService;
import com.ormi.mogakcote.notice.dto.response.NoticeResponse;
import com.ormi.mogakcote.post.application.PostService;
import com.ormi.mogakcote.post.dto.response.PostResponse;
import com.ormi.mogakcote.problem.application.AlgorithmService;
import com.ormi.mogakcote.problem.application.LanguageService;
import com.ormi.mogakcote.problem.application.PlatformService;
import com.ormi.mogakcote.problem.dto.response.AlgorithmResponse;
import com.ormi.mogakcote.problem.dto.response.LanguageResponse;
import com.ormi.mogakcote.problem.dto.response.PlatformResponse;
import com.ormi.mogakcote.user.application.UserService;
import com.ormi.mogakcote.user.dto.response.UserAuthResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@AllArgsConstructor
public class AdminController {

    private final PostService postService;
    private final NoticeService noticeService;
    private final LanguageService languageService;
    private final PlatformService platformService;
    private final AlgorithmService algorithmService;
    private final UserService userService;

    @GetMapping
    public ModelAndView adminPage(
        Model model) {

        List<UserAuthResponse> userAuthResponses = userService.getAll();
        List<PostResponse> postResponses = postService.getAllPosts();
        List<NoticeResponse> noticeResponses = noticeService.getNoticeList();
        List<LanguageResponse> languageResponses = languageService.getLanguageList();
        List<PlatformResponse> platformResponses = platformService.getPlatformList();
        List<AlgorithmResponse> algorithmResponses = algorithmService.getAlgorithmList();

        model.addAttribute("userList", userAuthResponses);
        model.addAttribute("postList", postResponses);
        model.addAttribute("noticeTop5List", noticeResponses);
        model.addAttribute("languageList", languageResponses);
        model.addAttribute("platformList", platformResponses);
        model.addAttribute("algorithmList", algorithmResponses);

        return new ModelAndView("admin/adminPageHtml");
    }

    @PutMapping("/convertBanned/{postId}")
    public ResponseEntity<?> convertBanned(
        @PathVariable(name = "postId") Long id
    ) {
        PostResponse updateBanned = postService.convertBanned(id);
        return ResponseEntity.ok(updateBanned);
    }
}