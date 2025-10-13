package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.station.StationCreateRequest;
import com.fire.fire_response_system.dto.station.StationResponse;
import com.fire.fire_response_system.service.StationService;
import io.swagger.v3.oas.annotations.Operation;
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

    @GetMapping
    @Operation(summary = "소방서 목록 조회", description = "전체 소방서 목록을 반환한다. 필요시 시도별 조회는 query 파라미터로 처리한다.")
    public ResponseEntity<List<StationResponse>> list(
            @RequestParam(required = false) String sido
    ) {
        return ResponseEntity.ok(stationService.list(sido));
    }

    @PostMapping
    @Operation(summary = "소방서 등록", description = "시도 + 소방서명을 기준으로 중복을 방지하고 신규 소방서를 등록한다.")
    public ResponseEntity<StationResponse> create(@Valid @RequestBody StationCreateRequest req) {
        return ResponseEntity.status(201).body(stationService.create(req));
    }
}
