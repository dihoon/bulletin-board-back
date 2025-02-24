package dihoon.bulletinboardback.controller;

import dihoon.bulletinboardback.api.PostApi;
import dihoon.bulletinboardback.domain.Post;
import dihoon.bulletinboardback.dto.AddPostRequest;
import dihoon.bulletinboardback.dto.ApiResponse;
import dihoon.bulletinboardback.dto.PostDto;
import dihoon.bulletinboardback.dto.UpdatePostRequest;
import dihoon.bulletinboardback.exception.InvalidTitleException;
import dihoon.bulletinboardback.exception.PostNotFoundException;
import dihoon.bulletinboardback.exception.TitleNotFoundException;
import dihoon.bulletinboardback.exception.UnauthorizedAccessException;
import dihoon.bulletinboardback.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/posts")
@RestController
public class PostApiController implements PostApi {
    private final PostService postService;

    @PostMapping()
    public ResponseEntity createPost(@RequestBody AddPostRequest request) {
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
            PostDto postDto = postService.convertToPostDto(post);
            ApiResponse response = new ApiResponse("Post retrieved successfully", postDto);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @GetMapping("/")
    public ResponseEntity getAllPosts(@RequestParam int page, @RequestParam int size, @RequestParam(defaultValue = "createdAt:desc") String sort) {
        try {
            Page<Post> posts = postService.getAllPosts(page, size, sort);
            ApiResponse response = new ApiResponse("Posts retrieved successfully", posts);
            return ResponseEntity.status(HttpStatus.OK).body(response);
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
        } catch (InvalidTitleException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity deletePost(@PathVariable long postId) {
        try {
            long deletedPostId = postService.deletePost(postId);

            ApiResponse response = new ApiResponse("Post deleted successfully", deletedPostId);

            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (UnauthorizedAccessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (PostNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
