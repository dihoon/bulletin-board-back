package dihoon.bulletinboardback.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SecuritySchemes({
        @SecurityScheme(name = "bearerToken", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
        @SecurityScheme(name = "cookie", type = SecuritySchemeType.APIKEY, in = SecuritySchemeIn.HEADER, paramName = "cookie")
})
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("bulletin-board-back")
                        .version("1.0")
                        .description("bulletin-board api 명세서입니다."));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        String[] paths = {"/api/**"};
        String[] packagesToScan = {"dihoon.bulletinboardback"};
        return GroupedOpenApi.builder().group("bulletin-board-back")
                .pathsToMatch(paths)
                .packagesToScan(packagesToScan)
                .build();
    }
}
