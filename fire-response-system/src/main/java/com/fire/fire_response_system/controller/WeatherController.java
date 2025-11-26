package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 프론트에서 호출할 기상청 단기예보 API 프록시 컨트롤러
 */
@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * 예시 호출:
     * GET /api/weather/village-forecast?baseDate=20210628&baseTime=0500&nx=55&ny=127
     */
    @GetMapping("/village-forecast")
    public ResponseEntity<String> getVillageForecast(
            @RequestParam String baseDate,
            @RequestParam String baseTime,
            @RequestParam int nx,
            @RequestParam int ny,
            @RequestParam(defaultValue = "1") int pageNo,
            @RequestParam(defaultValue = "1000") int numOfRows
    ) {
        String body = weatherService.getVillageForecast(
                baseDate, baseTime, nx, ny, pageNo, numOfRows
        );
        return ResponseEntity.ok(body);
    }
}
