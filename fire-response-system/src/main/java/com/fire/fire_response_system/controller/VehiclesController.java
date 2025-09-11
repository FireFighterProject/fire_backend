package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.vehicle.*;
import com.fire.fire_response_system.service.VehicleQueryService;
import com.fire.fire_response_system.service.VehiclesService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class VehiclesController {

    private final VehicleQueryService vehicleQueryService;
    private final VehiclesService vehiclesService;

    // --- 메타
    @GetMapping("/vehicle-types")
    @Operation(summary = "차종 목록 조회")
    public ResponseEntity<List<String>> getVehicleTypes() {
        return ResponseEntity.ok(vehicleQueryService.getVehicleTypes());
    }

    // --- 차량 등록
    @PostMapping("/vehicles")
    @Operation(summary = "차량 단건 등록", description = "stationId + callSign 중복 시 409")
    public ResponseEntity<VehicleResponse> create(@Valid @RequestBody VehicleCreateRequest req) {
        return ResponseEntity.status(201).body(vehiclesService.create(req));
    }

    // --- 차량 목록
    @GetMapping("/vehicles")
    @Operation(summary = "차량 목록 조회", description = "필터: stationId, status, typeName, callSignLike")
    public ResponseEntity<List<VehicleListItem>> list(
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String typeName,
            @RequestParam(required = false, name = "callSign") String callSignLike
    ) {
        return ResponseEntity.ok(vehiclesService.list(stationId, status, typeName, callSignLike));
    }

    // --- 차량 수정 (부분 업데이트)
    @PatchMapping("/vehicles/{id}")
    @Operation(summary = "차량 수정", description = "callSign 변경 시 동일 소방서 내 중복 검증")
    public ResponseEntity<VehicleResponse> update(
            @PathVariable Long id,
            @RequestBody VehicleUpdateRequest req
    ) {
        return ResponseEntity.ok(vehiclesService.update(id, req));
    }

    // --- 상태 변경
    @PatchMapping("/vehicles/{id}/status")
    @Operation(summary = "차량 상태 변경", description = "0=대기, 1=활동, 2=철수")
    public ResponseEntity<VehicleResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody VehicleStatusUpdateRequest req
    ) {
        return ResponseEntity.ok(vehiclesService.updateStatus(id, req.getStatus()));
    }

    // --- 집결지 토글/설정
    @PatchMapping("/vehicles/{id}/assembly")
    @Operation(summary = "집결지 토글/설정", description = "rallyPoint 미전달 시 토글, 값 전달 시 0/1 설정")
    public ResponseEntity<VehicleResponse> updateAssembly(
            @PathVariable Long id,
            @RequestBody(required = false) VehicleAssemblyUpdateRequest req
    ) {
        Integer value = (req == null) ? null : req.getRallyPoint();
        return ResponseEntity.ok(vehiclesService.updateAssembly(id, value));
    }
}
