package com.myproject.doc_rag.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProjectConfig {

    @Bean
    public OpenAPI openapi(){
        return new OpenAPI().info(new Info().title("Document RAG API").description("API documentation for Document RAG application").version("1.0.0").contact(new Contact().name("Tushar").email("tusharray1511@gmail.com").url("https://portfolio-tushar-rays-projects.vercel.app/")));

    }
}
