package dihoon.bulletinboardback.controller;

import dihoon.bulletinboardback.dto.AddUserRequest;
import dihoon.bulletinboardback.dto.LoginRequest;
import dihoon.bulletinboardback.dto.LoginResponse;
import dihoon.bulletinboardback.exception.InvalidEmailException;
import dihoon.bulletinboardback.exception.UserAlreadyExistsException;
import dihoon.bulletinboardback.jwt.TokenProvider;
import dihoon.bulletinboardback.service.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User API", description = "User API 입니다.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;
    private final TokenProvider tokenProvider;

    @PostMapping("/sign-up")
    public ResponseEntity signUp(@RequestBody AddUserRequest request) {
        try {
            userService.save(request);
        } catch (InvalidEmailException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }

    @PostMapping("/login")
    @Parameters( value = { @Parameter(name = "request", hidden = true), @Parameter(name = "request", hidden = true)})
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "login request payload",
            required = true, content = @Content(schema = @Schema(implementation = LoginRequest.class),
            examples = { @ExampleObject(value = "{ \"email\" : \"test@gmail.com\", \"password\" : \"1234\"}")}))
    public ResponseEntity login( HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authResult = (Authentication) request.getAttribute("authResult");
            UserDetails userDetails = (UserDetails) authResult.getPrincipal();

            System.out.println(authResult);

            String email = userDetails.getUsername();

            String accessToken = tokenProvider.generateAccessToken(email);

            String message = "Login Successful";

            LoginResponse loginResponse = new LoginResponse(true, message, accessToken);

            String refreshToken = tokenProvider.generateRefreshToken(email);

            userService.updateRefreshToken(email, refreshToken);

            Cookie refreshTokenCookie = tokenProvider.generateCookie(refreshToken, tokenProvider.getRefreshSecretKey(), "/api/auth");

            response.addCookie(refreshTokenCookie);

            return ResponseEntity.ok().body(loginResponse);
        } catch (AuthenticationException e) {
            LoginResponse loginResponse = new LoginResponse(false, e.getMessage(), null);
            System.out.println(loginResponse);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        } catch (Exception e) {
            LoginResponse loginResponse = new LoginResponse(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loginResponse);
        }
    }
}
