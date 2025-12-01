package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.gps.GpsSendRequest;
import com.fire.fire_response_system.service.GpsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "GPS API",
        description = "GPS 수신 및 차량 위치 조회 기능"
)
@RestController
@RequestMapping("/api/gps")
@RequiredArgsConstructor
public class GpsController {

    private final GpsService gpsService;

    /** GPS 수신 */
    @PostMapping("/send")
    @Operation(summary = "GPS 수신", description = "vehicleId, 위도(latitude), 경도(longitude)를 전달하면 위치를 저장합니다.")
    public ResponseEntity<String> receive(@RequestBody GpsSendRequest req) {
        gpsService.receive(req);
        return ResponseEntity.ok("GPS 수신 완료");
    }

    /** 특정 차량 위치 조회 */
    @GetMapping("/location/{vehicleId}")
    @Operation(summary = "특정 차량 현재 위치 조회")
    public ResponseEntity<?> getLocation(@PathVariable Long vehicleId) {
        return ResponseEntity.ok(gpsService.getLocation(vehicleId));
    }

    /** 소방서 차량 위치 조회 */
    @GetMapping("/station/{stationId}")
    @Operation(summary = "소방서 소속 차량 전체 현재 위치 조회")
    public ResponseEntity<?> getStationLocations(@PathVariable Long stationId) {
        return ResponseEntity.ok(gpsService.getStationLocations(stationId));
    }

    /** 모든 차량 GPS 조회 (GPS 등록 차량만) */
    @GetMapping("/all")
    @Operation(summary = "GPS 등록된 모든 차량 위치 조회")
    public ResponseEntity<?> getAllLocations() {
        return ResponseEntity.ok(gpsService.getAll());
    }
}
