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
    @Operation(summary = "소방서 목록 조회")
    public ResponseEntity<List<StationResponse>> list() {
        return ResponseEntity.ok(stationService.list());
    }

    @PostMapping
    @Operation(summary = "소방서 등록")
    public ResponseEntity<StationResponse> create(@Valid @RequestBody StationCreateRequest req) {
        return ResponseEntity.status(201).body(stationService.create(req));
    }
}
