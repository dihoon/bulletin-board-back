package dihoon.bulletinboardback.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dihoon.bulletinboardback.api.AuthApi;
import dihoon.bulletinboardback.dto.AddUserRequest;
import dihoon.bulletinboardback.dto.LoginResponse;
import dihoon.bulletinboardback.dto.SignUpResponse;
import dihoon.bulletinboardback.exception.InvalidEmailException;
import dihoon.bulletinboardback.exception.UserAlreadyExistsException;
import dihoon.bulletinboardback.jwt.TokenProvider;
import dihoon.bulletinboardback.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RequestMapping("/api/auth")
@RestController
public class AuthApiController implements AuthApi {
    private final TokenProvider tokenProvider;
    private final UserService userService;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(new SignUpResponse("User registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity login( HttpServletRequest request, HttpServletResponse response) {
        try {
            Authentication authResult = (Authentication) request.getAttribute("authResult");
            UserDetails userDetails = (UserDetails) authResult.getPrincipal();

            String email = userDetails.getUsername();

            String accessToken = tokenProvider.generateAccessToken(email);

            String message = "Login Successful";

            LoginResponse loginResponse = new LoginResponse(true, message, accessToken, email);

            String refreshToken = tokenProvider.generateRefreshToken(email);

            userService.updateRefreshToken(email, refreshToken);

            ResponseCookie refreshTokenCookie = tokenProvider.generateCookie(refreshToken, tokenProvider.getRefreshSecretKey(), "/api/auth");

            response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

            return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
        } catch (AuthenticationException e) {
            LoginResponse loginResponse = new LoginResponse(false, e.getMessage(), null, null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(loginResponse);
        } catch (Exception e) {
            LoginResponse loginResponse = new LoginResponse(false, e.getMessage(), null, null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(loginResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.status(HttpStatus.OK).body("Logout Successful");
    }

    @DeleteMapping()
    public ResponseEntity deleteAccount(HttpServletRequest request, HttpServletResponse response) {
        try {
            userService.deleteByUserId();
            return new ResponseEntity("User deleted successfully", HttpStatus.OK);
        } catch (UsernameNotFoundException e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity refreshAccessToken(HttpServletRequest request) {
        try {
            String refreshToken = TokenProvider.extractRefreshTokenFromRequest(request);

            if (refreshToken != null) {
                String email = tokenProvider.getClaims(refreshToken, tokenProvider.getRefreshSecretKey()).get("email", String.class);

                UserDetails userDetails = userService.loadUserByUsername(email);

                String orignRefreshToken = userService.findByEmail(email).getRefreshToken();

                if (refreshToken.equals(orignRefreshToken) && tokenProvider.validateToken(refreshToken, tokenProvider.getRefreshSecretKey())) {
                    String newAccessToken = tokenProvider.generateAccessToken(email);

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());

                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    ObjectMapper objectMapper = new ObjectMapper();

                    Map<String, String> map = new HashMap<>();
                    map.put("email", email);
                    map.put("token", newAccessToken);

                    String response = objectMapper.writeValueAsString(map);

                    return ResponseEntity.ok().body(response);
                }
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("refresh token is missing");
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
