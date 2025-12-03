package com.fire.fire_response_system.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * application.yml → solapi.* 값을 읽어오는 설정 클래스
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "solapi")
public class SolapiProperties {

    private String apiKey;
    private String apiSecret;
    private String from;
}
