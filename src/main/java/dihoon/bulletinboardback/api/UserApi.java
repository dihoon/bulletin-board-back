package dihoon.bulletinboardback.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;

@Tag(name = "User API", description = "User API 입니다.")
public interface UserApi {

    @Operation(summary = "회원 정보 조회", security = {@SecurityRequirement(name = "accessToken")})
    ResponseEntity getUserInfo(HttpServletRequest request);
}
