package com.eve.eve.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("EVE Healthcare API")
                        .version("1.0")
                        .description(
                                "Diagnostic centre booking and payment API. "
                                        + "Use a JWT bearer token for protected endpoints. "
                                        + "Creating centres and managing tests requires the ADMIN role."
                        ))
                .addSecurityItem(
                        new SecurityRequirement()
                                .addList("bearerAuth")
                )
                .components(
                        new Components()
                                .addSecuritySchemes(
                                        "bearerAuth",
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT")
                                )
                );
    }

    @Bean
    public OpenApiCustomizer customizeOperations() {

        return openApi -> openApi.getPaths().forEach((path, pathItem) -> {
            String tag = tagForPath(path);
            boolean publicPath = isPublicPath(path);

            pathItem.readOperationsMap().forEach((method, operation) -> {
                if (tag != null) {
                    operation.setTags(List.of(tag));
                }

                if (publicPath) {
                    operation.setSecurity(List.of());
                }

                if (method == PathItem.HttpMethod.POST
                        && isAdminPath(path)) {
                    String description = operation.getDescription();
                    operation.setDescription(
                            (description == null || description.isBlank()
                                    ? ""
                                    : description + "\n\n")
                                    + "Requires the ADMIN role."
                    );
                }
            });
        });
    }

    private String tagForPath(String path) {
        if (path.startsWith("/api/auth")) {
            return "Authentication";
        }
        if (path.startsWith("/api/centres")) {
            return "Diagnostic Centres";
        }
        if (path.startsWith("/api/tests")) {
            return "Diagnostic Tests";
        }
        if (path.startsWith("/api/bookings")) {
            return "Bookings";
        }
        if (path.startsWith("/api/payments")) {
            return "Payments";
        }
        if (path.startsWith("/api/health")
                || path.startsWith("/actuator/health")) {
            return "Health";
        }
        return null;
    }

    private boolean isPublicPath(String path) {
        return path.equals("/api/auth/signup")
                || path.equals("/api/auth/login")
                || path.equals("/api/health")
                || path.equals("/actuator/health");
    }

    private boolean isAdminPath(String path) {
        return path.startsWith("/api/centres/")
                || path.equals("/api/centres")
                || path.startsWith("/api/tests/");
    }
}