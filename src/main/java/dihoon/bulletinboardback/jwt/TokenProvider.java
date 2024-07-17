package dihoon.bulletinboardback.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
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

    public Cookie generateCookie(String token, SecretKey key, String path) {
        String tokenType = getClaims(token, key).get("tokenType").toString();

        int iat = getClaims(token, key).get("iat", Integer.class);
        int exp = getClaims(token, key).get("exp", Integer.class);

        int expiration = exp - iat;

        Cookie cookie = new Cookie(tokenType, token);
        cookie.setPath(path);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true);
        cookie.setMaxAge(expiration);

        return cookie;
    }
}
