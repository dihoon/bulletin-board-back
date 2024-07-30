package dihoon.bulletinboardback.controller;

import dihoon.bulletinboardback.api.UserApi;
import dihoon.bulletinboardback.domain.Post;
import dihoon.bulletinboardback.domain.User;
import dihoon.bulletinboardback.dto.ApiResponse;
import dihoon.bulletinboardback.dto.UserInfoResponse;
import dihoon.bulletinboardback.jwt.TokenProvider;
import dihoon.bulletinboardback.service.PostService;
import dihoon.bulletinboardback.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User API", description = "User API 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController implements UserApi {
    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PostService postService;

    @GetMapping()
    public ResponseEntity getUserInfo(HttpServletRequest request) {
        String accessToken = request.getHeader("Authorization").split("Bearer ")[1];

        String email = tokenProvider.getClaims(accessToken, tokenProvider.getAccessSecretKey()).get("email").toString();

        User user = userService.findByEmail(email);

        UserInfoResponse userInfoResponse = new UserInfoResponse(user.getEmail(), user.getRole());

        ApiResponse<UserInfoResponse> apiResponse = new ApiResponse("user info retrieved successfully", userInfoResponse);

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/posts")
    public ResponseEntity getUserPosts(@RequestParam int page, @RequestParam int size, @RequestParam(defaultValue = "createdAt:desc") String sort) {
        try {
            Page<Post> posts = postService.getPostByUser(page, size, sort);
            ApiResponse response = new ApiResponse("Your posts retrieved successfully", posts);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
