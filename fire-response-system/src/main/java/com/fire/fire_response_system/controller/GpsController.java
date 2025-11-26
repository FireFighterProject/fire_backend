// src/main/java/com/fire/fire_response_system/controller/GpsController.java
package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.gps.GpsSendRequest;
import com.fire.fire_response_system.dto.gps.GpsSendBatchRequest;
import com.fire.fire_response_system.dto.gps.MapStatsRequest;
import com.fire.fire_response_system.service.GpsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
public class GpsController {

    private final GpsService gpsService;

    /** 단건 GPS 수신 */
    @PostMapping("/send")
    @Operation(summary = "단건 GPS 수신")
    public ResponseEntity<?> send(@Valid @RequestBody GpsSendRequest req) {
        gpsService.receive(req);
        // 일단 바디 없이 200만
        return ResponseEntity.ok().build();
    }

    /** 배치 GPS 수신 */
    @PostMapping("/send-all")
    @Operation(summary = "배치 GPS 수신")
    public ResponseEntity<?> sendAll(@Valid @RequestBody GpsSendBatchRequest req) {
        int count = gpsService.receiveAll(req);
        // 나중에 GpsSendBatchResponse 로 바꿔도 됨
        return ResponseEntity.ok(count);
    }

    /** 차량 GPS 로그 조회 */
    @GetMapping("/logs")
    @Operation(summary = "차량 GPS 로그 조회")
    public ResponseEntity<?> logs(
            @RequestParam Long vehicleId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(gpsService.logs(vehicleId, from, to, page, size));
    }

    /** 소방서별 GPS 수신 상태 조회 */
    @GetMapping("/status")
    @Operation(summary = "소방서별 차량 GPS 수신 상태")
    public ResponseEntity<?> status(
            @RequestParam Long stationId,
            @RequestParam(defaultValue = "5") Integer withinMinutes
    ) {
        return ResponseEntity.ok(gpsService.status(stationId, withinMinutes));
    }

    /** 소방서 내 특정 차량들의 마지막 위치 */
    @GetMapping("/last-locations")
    @Operation(summary = "소방서 내 차량 마지막 위치 조회")
    public ResponseEntity<?> lastLocations(
            @RequestParam Long stationId,
            @RequestParam List<Long> vehicleIds
    ) {
        return ResponseEntity.ok(gpsService.lastLocations(stationId, vehicleIds));
    }

    /** 지도 드래그 영역 차량 및 통계 조회 */
    @PostMapping("/map/stats")
    @Operation(summary = "지도 드래그 영역 차량 및 통계 조회", description = "활동(status=1) 차량만 포함")
    public ResponseEntity<?> mapStats(@RequestBody MapStatsRequest req) {
        return ResponseEntity.ok(gpsService.mapStats(req));
    }
}
