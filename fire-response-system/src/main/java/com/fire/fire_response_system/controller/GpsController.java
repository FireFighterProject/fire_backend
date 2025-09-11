package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.gps.*;
import com.fire.fire_response_system.service.GpsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
public class GpsController {

    private final GpsService gpsService;

    @PostMapping("/send")
    @Operation(summary = "GPS 단건 수신 저장 + 최신위치 갱신")
    public ResponseEntity<Void> send(@Valid @RequestBody GpsSendRequest req) {
        gpsService.receive(req);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/send-all")
    @Operation(summary = "GPS 배치 수신(여러 건)")
    public ResponseEntity<GpsSendBatchResponse> sendAll(@Valid @RequestBody GpsSendBatchRequest req) {
        return ResponseEntity.ok(gpsService.receiveAll(req));
    }

    @GetMapping("/logs")
    @Operation(summary = "GPS 로그 조회", description = "vehicleId 필수, from/to(ISO-8601) 선택, page/size")
    public ResponseEntity<Page<GpsLogItem>> logs(
            @RequestParam Long vehicleId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(gpsService.logs(vehicleId, from, to, page, size));
    }

    @GetMapping("/status")
    @Operation(summary = "차량 온라인 여부", description = "withinMinutes(기본 3분) 이내 업데이트된 차량을 online=true")
    public ResponseEntity<List<VehicleStatusItem>> status(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) Integer withinMinutes
    ) {
        return ResponseEntity.ok(gpsService.status(stationId, withinMinutes));
    }

    @GetMapping("/last-locations")
    @Operation(summary = "차량 최신 좌표 목록", description = "stationId 또는 vehicleIds 중 하나 사용")
    public ResponseEntity<List<VehicleLocationItem>> lastLocations(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) List<Long> vehicleIds
    ) {
        return ResponseEntity.ok(gpsService.lastLocations(stationId, vehicleIds));
    }

    /** ✅ 지도 드래그 영역 차량 및 통계 조회 */
    @PostMapping("/map/stats")
    @Operation(summary = "지도 드래그 영역 차량 및 통계 조회", description = "활동(status=1) 차량만 포함")
    public ResponseEntity<MapStatsResponse> mapStats(@RequestBody MapStatsRequest req) {
        return ResponseEntity.ok(gpsService.mapStats(req));
    }
}
