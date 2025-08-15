package com.fire.fire_response_system.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "fire_response_system API",
                version = "v1",
                description = "재난대응 시스템 백엔드 API 문서"
        )
)
public class OpenApiConfig {}
