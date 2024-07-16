package dihoon.bulletinboardback.api;

import dihoon.bulletinboardback.dto.AddUserRequest;
import dihoon.bulletinboardback.dto.LoginRequest;
import dihoon.bulletinboardback.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

@Tag(name = "User API", description = "User API 입니다.")
public interface UserApi {

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(implementation = LoginResponse.class), mediaType = "application/json")),})
    ResponseEntity signUp(AddUserRequest request);

    @Parameters( value = { @Parameter(name = "request", hidden = true), @Parameter(name = "request", hidden = true)})
    @RequestBody(description = "login request payload",
            required = true, content = @Content(schema = @Schema(implementation = LoginRequest.class),
            examples = { @ExampleObject(value = "{ \"email\" : \"test@gmail.com\", \"password\" : \"1234\"}")}))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class), mediaType = "application/json"),
                    headers = @Header(name = "Set-Cookie", description = "refreshToken", schema = @Schema(type = "string")))
    })
    ResponseEntity login(HttpServletRequest request, HttpServletResponse response);
}
