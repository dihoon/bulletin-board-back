package dihoon.bulletinboardback.filter;

import dihoon.bulletinboardback.constant.PublicUrl;
import dihoon.bulletinboardback.jwt.TokenProvider;
import dihoon.bulletinboardback.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final UserService userService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            if (Arrays.stream(PublicUrl.getUrls()).anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()))) {
                filterChain.doFilter(request, response);
                return;
            }

            String accessToken = extractJwtFromRequest(request);

            if (accessToken != null && tokenProvider.validateToken(accessToken, tokenProvider.getAccessSecretKey())) {
                String email = tokenProvider.getClaims(accessToken, tokenProvider.getAccessSecretKey()).get("email", String.class);
                UserDetails userDetails = userService.loadUserByUsername(email);

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getWriter().write("Invalid or missing access token");
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred processing the authentication token");
        }
    }

//    private void refreshAccessToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String refreshToken = extractRefreshTokenFromRequest(request);
//        if (refreshToken != null && tokenProvider.validateToken(refreshToken, tokenProvider.getRefreshSecretKey())) {
//            String email = tokenProvider.getClaims(refreshToken, tokenProvider.getAccessSecretKey()).get("email", String.class);
//            UserDetails userDetails = userService.loadUserByUsername(email);
//
//            String newAccessToken = tokenProvider.generateAccessToken(email);
//
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null, userDetails.getAuthorities());
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            filterChain.doFilter(request, response);
//        } else {
//            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        }
//    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        logger.info("Bearer token: " + bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
