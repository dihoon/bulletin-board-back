package dihoon.bulletinboardback.constant;

import java.util.Arrays;

public enum PublicUrl {
    ROOT("/"),
    LOGIN("/api/auth/login"),
    SIGN_UP("/api/auth/sign-up"),
    ERROR("/error"),
    SWAGGER_UI("/swagger-ui/**"),
    API_DOCS("/api-docs/**"),
    REFRESH("/api/auth/refresh"),
    GETPOST("/api/posts/{postId}");

    private final String url;

    PublicUrl (String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public static String[] getUrls() {
        return Arrays.stream(PublicUrl.values())
                .map(PublicUrl::getUrl)
                .toArray(String[]::new);
    }

}
