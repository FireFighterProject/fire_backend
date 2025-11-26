package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Tag(name = "Weather API", description = "기상청 단기예보(4.3단기예보조회) API를 백엔드 프록시로 제공합니다.")
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping("/village-forecast")
    @Operation(
            summary = "기상청 단기예보 조회 (Village Forecast)",
            description = """
                    기상청 단기예보 API(4.3 단기예보 조회)를 백엔드를 통해 호출합니다.<br>
                    - 기상청 `VilageFcstInfoService_2.0/getVilageFcst` API를 그대로 전달합니다.<br>
                    - 프론트는 기상청 API를 직접 호출할 필요 없이, 이 엔드포인트만 호출하면 됩니다.<br><br>
                    ⚠ 참고사항:<br>
                    - base_date: 발표 일자 (예: 20210628)<br>
                    - base_time: 발표 시각 (예: 0500)<br>
                    - nx/ny: 격자 좌표<br>
                    """,
            parameters = {
                    @Parameter(name = "baseDate", description = "발표일자 (YYYYMMDD)", required = true, example = "20210628"),
                    @Parameter(name = "baseTime", description = "발표시각 (HHmm)", required = true, example = "0500"),
                    @Parameter(name = "nx", description = "예보지점 X 좌표", required = true, example = "55"),
                    @Parameter(name = "ny", description = "예보지점 Y 좌표", required = true, example = "127"),
                    @Parameter(name = "pageNo", description = "페이지 번호 (default: 1)", example = "1"),
                    @Parameter(name = "numOfRows", description = "한 페이지 결과 수 (default: 1000)", example = "1000")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(mediaType = "application/json")),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청"),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류")
            }
    )
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
