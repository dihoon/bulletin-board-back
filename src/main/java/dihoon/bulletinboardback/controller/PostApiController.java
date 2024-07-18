package dihoon.bulletinboardback.controller;

import dihoon.bulletinboardback.api.PostApi;
import dihoon.bulletinboardback.dto.AddPostRequest;
import dihoon.bulletinboardback.dto.ApiResponse;
import dihoon.bulletinboardback.exception.TitleNotFoundException;
import dihoon.bulletinboardback.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostApiController implements PostApi {
    private final PostService postService;

    @PostMapping("/")
    public ResponseEntity createPost(AddPostRequest request) {
        try {
            postService.createPost(request);
        } catch (TitleNotFoundException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        ApiResponse response = new ApiResponse("Post registered successfully", null);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
