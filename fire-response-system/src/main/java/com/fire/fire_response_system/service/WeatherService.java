package com.fire.fire_response_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 기상청 단기예보 API 호출 서비스
 */
@Service
public class WeatherService {

    @Value("${weather.api-key}")
    private String apiKey;

    @Value("${weather.base-url}")
    private String baseUrl;

    // 간단히 new 로 사용 (필요하면 @Bean 으로 분리해도 됨)
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 기상청 단기예보(4.3 VilageFcst) 조회
     */
    public String getVillageForecast(
            String baseDate,
            String baseTime,
            int nx,
            int ny,
            int pageNo,
            int numOfRows
    ) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("dataType", "JSON")          // 프론트에서 쓰기 편하게 JSON으로 고정
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .queryParam("authKey", apiKey)
                .toUriString();

        // 그대로 String으로 리턴해서 컨트롤러 -> 프론트로 전달
        return restTemplate.getForObject(url, String.class);
    }
}
