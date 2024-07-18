package dihoon.bulletinboardback.api;

import dihoon.bulletinboardback.dto.AddPostRequest;
import dihoon.bulletinboardback.dto.UpdatePostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Post API", description = "포스팅 API 입니다.")
public interface PostApi {

    @Operation(summary = "게시글 생성", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity createPost(AddPostRequest post);

    @Operation(summary = "게시글 조회")
    ResponseEntity getPostById (long postId);

    @Operation(summary = "게시글 업데이트", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity updatePost(@PathVariable long postId, @RequestBody UpdatePostRequest request);

    @Operation(summary = "게시글 삭제", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity deletePost(@PathVariable long postId);
}
