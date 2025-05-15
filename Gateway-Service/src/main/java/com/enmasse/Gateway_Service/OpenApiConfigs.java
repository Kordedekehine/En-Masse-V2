package com.enmasse.Gateway_Service;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.config.EnableWebFlux;

import java.util.List;

@OpenAPIDefinition
@EnableWebFlux
public class OpenApiConfigs {


    @Bean
    public OpenAPI customOpenAPI() {

        return new OpenAPI()
                .info(new Info()
                        .title("EnMasse V2 API")
                        .version("v2")
                        .description("API documentation for the EnMasse App ")
                        .termsOfService("http://example.com/terms/")
                        .contact(new Contact()
                                .name("Korede")
                                .email("salamkorede345@gmail.com")
                                .url("http://korede.vercel.app"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://korede.vercel.app").description("Development Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("Bearer ",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .security(List.of(
                        new SecurityRequirement().addList("Bearer ")
                ));
    }
}
