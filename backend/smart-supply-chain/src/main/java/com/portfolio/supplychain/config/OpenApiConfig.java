package com.portfolio.supplychain.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI smartSupplyChainOpenAPI() {
        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Local development server");

        Contact contact = new Contact()
                .name("Sowmya Jenniefer")
                .url("https://github.com/sowmyajenniefer");

        Info info = new Info()
                .title("Smart Supply Chain Order Management Platform API")
                .version("1.0.0")
                .description("""
                        API documentation for the Smart Supply Chain Order Management Platform.
                        This project demonstrates Spring Boot REST APIs, PostgreSQL integration,
                        layered architecture, product management, inventory workflows,
                        order processing, Kafka event flow, Redis caching, Docker, and Kubernetes.
                        """)
                .contact(contact);

        return new OpenAPI()
                .servers(List.of(localServer))
                .info(info);
    }
}