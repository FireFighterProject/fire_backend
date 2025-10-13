package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.station.StationCreateRequest;
import com.fire.fire_response_system.dto.station.StationResponse;
import com.fire.fire_response_system.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fire-stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    /**
     * 소방서 목록 조회 (시도별 필터 지원)
     * 예: /api/fire-stations?sido=경북
     */
    @GetMapping
    @Operation(
            summary = "소방서 목록 조회",
            description = "전체 소방서 목록을 반환합니다. "
                    + "sido 파라미터를 지정하면 해당 지역의 소방서만 반환합니다."
    )
    public ResponseEntity<List<StationResponse>> list(
            @Parameter(description = "시도명 (예: 경북, 서울 등)", example = "경북")
            @RequestParam(required = false) String sido
    ) {
        return ResponseEntity.ok(stationService.list(sido));
    }

    /**
     * 소방서 등록
     * 시도 + 이름 기준으로 중복 방지
     */
    @PostMapping
    @Operation(
            summary = "소방서 등록",
            description = "시도(sido) + 소방서명(name)을 기준으로 중복을 방지하고 새 소방서를 등록합니다."
    )
    public ResponseEntity<StationResponse> create(
            @Valid @RequestBody StationCreateRequest req
    ) {
        return ResponseEntity.status(201).body(stationService.create(req));
    }
}
