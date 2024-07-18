package dihoon.bulletinboardback.api;

import dihoon.bulletinboardback.dto.AddPostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Post API", description = "포스팅 API 입니다.")
public interface PostApi {

    @Operation(summary = "게시글 생성", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity createPost(AddPostRequest post);

    @Operation(summary = "게시글 조회")
    ResponseEntity getPostById (long postId);
}
