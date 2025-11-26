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

    private final RestTemplate restTemplate = new RestTemplate();

    public String getVillageForecast(
            String baseDate,
            String baseTime,
            int nx,
            int ny,
            int pageNo,
            int numOfRows
    ) {

        if (baseDate == null || baseTime == null)
            throw new IllegalArgumentException("baseDate, baseTime은 필수입니다.");

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("pageNo", pageNo)
                .queryParam("numOfRows", numOfRows)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", nx)
                .queryParam("ny", ny)
                .queryParam("authKey", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}
