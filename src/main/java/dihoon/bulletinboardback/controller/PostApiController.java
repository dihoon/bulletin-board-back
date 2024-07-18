package dihoon.bulletinboardback.controller;

import dihoon.bulletinboardback.api.PostApi;
import dihoon.bulletinboardback.domain.Post;
import dihoon.bulletinboardback.dto.AddPostRequest;
import dihoon.bulletinboardback.dto.ApiResponse;
import dihoon.bulletinboardback.dto.UpdatePostRequest;
import dihoon.bulletinboardback.exception.PostNotFoundException;
import dihoon.bulletinboardback.exception.TitleNotFoundException;
import dihoon.bulletinboardback.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{postId}")
    public ResponseEntity getPostById(@PathVariable long postId) {
        try {
            Post post = postService.getPostById(postId);
            ApiResponse response = new ApiResponse("Post retrieved successfully", post);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/{postId}")
    public ResponseEntity updatePost(@PathVariable long postId, @RequestBody UpdatePostRequest request) {
        try {
            Post post = postService.updatePost(postId, request);
            ApiResponse response = new ApiResponse("Post updated successfully", post);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
