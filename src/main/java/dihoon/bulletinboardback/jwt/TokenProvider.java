package dihoon.bulletinboardback.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Date;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt")
public class TokenProvider {
    private String issuer;
    private String accessKey;
    private String refreshKey;
    private SecretKey accessSecretKey;
    private SecretKey refreshSecretKey;
    private long accessExpiration;
    private long refreshExpiration;

    @PostConstruct
    public void init() {
        byte[] secretKeyBytes = accessKey.getBytes();
        accessSecretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");

        byte[] refreshKeyBytes = refreshKey.getBytes();
        refreshSecretKey = new SecretKeySpec(refreshKeyBytes, "HmacSHA256");
    }

    public String generateToken(String email, String tokenType, SecretKey key, long expiration) {
        Instant now = Instant.now();

        Instant expiry = now.plusSeconds(expiration);

        return Jwts.builder()
                .claim("email", email)
                .claim("tokenType", tokenType)
                .issuer(issuer)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    public String generateAccessToken(String email) {
        String accessToken = generateToken(email, "accessToken", accessSecretKey, accessExpiration);
        return accessToken;
    }

    public String generateRefreshToken(String email) {
        String refreshToken = generateToken(email, "refreshToken", refreshSecretKey, refreshExpiration);
        return refreshToken;
    }

    public boolean validateToken(String token, SecretKey key) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaims(String token, SecretKey key) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
    }

    public ResponseCookie generateCookie(String token, SecretKey key, String path) {
        String tokenType = getClaims(token, key).get("tokenType").toString();

        int iat = getClaims(token, key).get("iat", Integer.class);
        int exp = getClaims(token, key).get("exp", Integer.class);

        int expiration = exp - iat;

        ResponseCookie cookie = ResponseCookie.from(tokenType, token)
                .path(path)
                .httpOnly(true)
                .secure(true)
                .maxAge(expiration)
                .sameSite("None")
                .build();

        return cookie;
    }

    public static String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public static String extractRefreshTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refreshToken")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    public static Cookie resetCookie(Cookie cookie) {
        String name = cookie.getName();
        Cookie newCookie = new Cookie(name, null);
        newCookie.setPath("/api/auth");
        newCookie.setMaxAge(0);
        newCookie.setHttpOnly(true);
        return newCookie;
    }
}
