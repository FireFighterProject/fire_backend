package com.fire.fire_response_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** 프론트(5174)에서 오는 요청 허용 – 네 스타일 유지(심플) */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        // allowCredentials(true)를 쓸 땐 allowedOriginPatterns 권장
        cfg.setAllowedOriginPatterns(List.of(
                "http://localhost:5174",
                "http://localhost:*",
                "https://fire.rjsgud.com"
        ));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        // 프리플라이트 허용(브라우저가 자동 보내는 OPTIONS)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // springdoc + swagger-ui 전부 허용
                        .requestMatchers(
                                "/v3/api-docs/**",      // springdoc 2.x 표준 경로
                                "/api-docs/**",         // 혹시 모를 커스텀 경로 대비
                                "/swagger-ui.html",
                                "/swagger-ui/**"
                        ).permitAll()

                        // 개발 중 공개 API
                        .requestMatchers("/api/**").permitAll()

                        // 그 외는 일단 허용(지금 네 원래 코드 흐름 유지)
                        .anyRequest().permitAll()
                )
                .httpBasic(b -> b.disable())
                .formLogin(f -> f.disable());

        return http.build();
    }
}
