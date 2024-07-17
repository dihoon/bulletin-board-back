package dihoon.bulletinboardback.config;

import dihoon.bulletinboardback.constant.PublicUrl;
import dihoon.bulletinboardback.domain.User;
import dihoon.bulletinboardback.filter.CustomUsernamePasswordAuthenticationFilter;
import dihoon.bulletinboardback.filter.JwtAuthenticationFilter;
import dihoon.bulletinboardback.jwt.TokenProvider;
import dihoon.bulletinboardback.service.UserService;
import jakarta.servlet.http.Cookie;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final UserService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth.requestMatchers(PublicUrl.getUrls()).permitAll()
                        .anyRequest().permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(login->login.disable())
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .addLogoutHandler(logoutHandler()))
                .addFilterBefore(new CustomUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, userService), LogoutFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider () throws Exception {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager () throws Exception {
        DaoAuthenticationProvider provider = daoAuthenticationProvider();
        return new ProviderManager(provider);
    }

    @Bean
    public CustomUsernamePasswordAuthenticationFilter customUsernamePasswordAuthenticationFilter () throws Exception {
        CustomUsernamePasswordAuthenticationFilter filter = new CustomUsernamePasswordAuthenticationFilter();
        filter.setRequiresAuthenticationRequestMatcher(new AntPathRequestMatcher(PublicUrl.LOGIN.getUrl(), "POST"));
        filter.setFilterProcessesUrl(PublicUrl.LOGIN.getUrl());
        filter.setUsernameParameter("email");
        filter.setAuthenticationManager(authenticationManager());
        return filter;
    }

    @Bean
    public LogoutHandler logoutHandler () {
        return (request, response, auth) -> {
            String refreshToken = TokenProvider.extractRefreshTokenFromRequest(request);
            User user = userService.findByEmail(auth.getName());
            if (refreshToken != null && refreshToken.equals(user.getRefreshToken())) {
                userService.updateRefreshToken(user.getEmail(), null);
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler () {
        return (request, response, authentication) -> {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("refreshToken")) {
                        Cookie newCookie = TokenProvider.resetCookie(cookie);
                        response.addCookie(newCookie);
                    }
                }
            }
            response.setStatus(HttpStatus.OK.value());
        };
    }
}
