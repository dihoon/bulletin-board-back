package dihoon.bulletinboardback.api;

import dihoon.bulletinboardback.dto.AddUserRequest;
import dihoon.bulletinboardback.dto.LoginRequest;
import dihoon.bulletinboardback.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth API", description = "Auth API 입니다.")
public interface AuthApi {

    @Operation(summary = "회원가입")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class),
                            mediaType = "application/json")),})
    ResponseEntity signUp(AddUserRequest request);

    @Operation(summary = "로그인")
    @Parameters( value = { @Parameter(name = "request", hidden = true), @Parameter(name = "request", hidden = true)})
    @RequestBody(description = "login request payload",
            required = true, content = @Content(schema = @Schema(implementation = LoginRequest.class),
            examples = { @ExampleObject(value = "{ \"email\" : \"test@gmail.com\", \"password\" : \"1234\"}")}))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class), mediaType = "application/json"),
                    headers = @Header(name = "Set-Cookie", description = "refreshToken", schema = @Schema(type = "string")))})
    ResponseEntity login(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "액세스 토큰 재발급", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity refreshAccessToken(HttpServletRequest request);

    @Operation(summary = "로그아웃", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity logout(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "회원탈퇴", security = { @SecurityRequirement(name = "accessToken")})
    ResponseEntity deleteAccount(HttpServletRequest request, HttpServletResponse response);
}
