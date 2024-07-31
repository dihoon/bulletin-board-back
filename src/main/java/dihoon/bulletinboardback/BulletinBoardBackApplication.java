package dihoon.bulletinboardback;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.TimeZone;

@SpringBootApplication
public class BulletinBoardBackApplication {

    @Value("${cors.allowed-origins}")
    private String[] allowedOrigins;

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("GMT+09:00"));

        SpringApplication.run(BulletinBoardBackApplication.class, args);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedMethods("*")
                        .allowedOriginPatterns(allowedOrigins)
                        .allowCredentials(true).exposedHeaders("Set-Cookie");
            }
        };
    }


}
