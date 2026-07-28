package com.exopet.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI exopetOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ExoPet 异宠小愈 API")
                        .description("异宠小愈微服务后端接口文档")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("程浩男")
                                .email("chenghaonan@exopet.com")));
    }
}
