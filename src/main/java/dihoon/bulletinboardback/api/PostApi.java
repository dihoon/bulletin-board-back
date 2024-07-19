package dihoon.bulletinboardback.api;

import dihoon.bulletinboardback.dto.AddPostRequest;
import dihoon.bulletinboardback.dto.UpdatePostRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "Post API", description = "포스팅 API 입니다.")
public interface PostApi {

    @Operation(summary = "게시글 생성", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity createPost(AddPostRequest post);

    @Operation(summary = "게시글 조회")
    ResponseEntity getPostById (long postId);

    @Operation(summary = "게시글 페이지네이션 조회")
    @Parameters(value = {
            @Parameter(name = "sort", description = "POST 필드, asc 또는 desc", content=@Content(schema=@Schema(type="array", implementation=String[].class))),
    })
    ResponseEntity getAllPosts(int page, int size, String... sort);

    @Operation(summary = "게시글 업데이트", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity updatePost(@PathVariable long postId, UpdatePostRequest request);

    @Operation(summary = "게시글 삭제", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity deletePost(@PathVariable long postId);
}
