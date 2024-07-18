package dihoon.bulletinboardback.constant;

import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PublicUrl {
    ROOT("/"),
    LOGIN("/api/auth/login"),
    SIGN_UP("/api/auth/sign-up"),
    ERROR("/error"),
    SWAGGER_UI("/swagger-ui/**"),
    API_DOCS("/api-docs/**"),
    REFRESH("/api/auth/refresh"),
    GETPOST("GET", "/api/posts/{postId}"),
    GETPOSTS("GET", "/api/posts/**");

    private final String httpMethod;
    private final String url;

    PublicUrl (String url) {
        this.httpMethod = null;
        this.url = url;
    }

    PublicUrl (String httpMethod, String url) {
        this.httpMethod = httpMethod;
        this.url = url;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUrl() {
        return url;
    }

    public static List<RequestMatcher> getRequestMatchers() {
        return Arrays.stream(PublicUrl.values())
                .map(publicUrl -> new AntPathRequestMatcher(publicUrl.getUrl(), publicUrl.getHttpMethod()))
                .collect(Collectors.toList());
    }

}
