package com.apigateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.openapi.title}")
    private String title;

    @Value("${app.openapi.version}")
    private String version;

    @Value("${app.openapi.server.prod.prefix}")
    private String prodPrefix;

    @Value("${app.openapi.server.local.prefix:http://localhost:8080/}")
    private String localPrefix;

    @Value("${spring.profiles.active:dev}")
    private String activeProfile;

    @Bean
    public OpenAPI customOpenAPI() {

        List<Server> servers = new ArrayList<>();

        if (!"prod".equals(activeProfile)) {
            servers.add(new Server()
                    .url(localPrefix)
                    .description("Local Dev"));
        }else{
            servers.add(new Server()
                    .url(prodPrefix)
                    .description("Production Server"));
        }

        return new OpenAPI()
                .info(new Info().title(title).version(version))
                .servers(servers);
    }
}