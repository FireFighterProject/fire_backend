package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.domain.dispatch.DispatchOrder;
import com.fire.fire_response_system.dto.dispatch.AssignVehicleRequest;
import com.fire.fire_response_system.dto.dispatch.CreateDispatchOrderRequest;
import com.fire.fire_response_system.dto.dispatch.CreateDispatchOrderResponse;
import com.fire.fire_response_system.dto.dispatch.UpdateDispatchOrderRequest;
import com.fire.fire_response_system.dto.dispatch.UpdateVehicleStatusRequest;
import com.fire.fire_response_system.service.DispatchOrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatch-orders")
@RequiredArgsConstructor
public class DispatchOrderController {
    private final DispatchOrderService service;

    @PostMapping
    @Operation(summary = "출동 명령 생성", description = "제목/내용으로 출동 명령을 생성 (초기 상태 DRAFT)")
    public ResponseEntity<CreateDispatchOrderResponse> create(@RequestBody CreateDispatchOrderRequest req) {
        Long id = service.create(req);
        return ResponseEntity.status(201).body(new CreateDispatchOrderResponse(id, "created"));
    }

    @GetMapping
    @Operation(summary = "출동 명령 조회", description = "상태별 필터 (status=0|1|2)")
    public ResponseEntity<List<DispatchOrder>> list(@RequestParam(required = false) Integer status) {
        return ResponseEntity.ok(service.list(status));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "출동 명령 수정", description = "제목/내용 수정 (필드별 부분수정)")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody UpdateDispatchOrderRequest req) {
        service.update(id, req);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "출동 명령 삭제", description = "작성중/종료 상태만 삭제 가능")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/send")
    @Operation(summary = "출동 명령 발송", description = "상태를 SENT로 전환하고 편성 차량 상태를 SENT로 표시")
    public ResponseEntity<Void> send(@PathVariable Long id) {
        service.send(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/end")
    @Operation(summary = "출동 명령 종료", description = "상태를 ENDED 로 전환")
    public ResponseEntity<Void> end(@PathVariable Long id) {
        service.end(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "차량 편성 추가", description = "해당 출동 명령에 차량을 편성 (중복 시 무시)")
    public ResponseEntity<Void> assign(@PathVariable Long id, @RequestBody AssignVehicleRequest req) {
        service.assignVehicle(id, req.getVehicleId());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/assign/{vehicleId}")
    @Operation(summary = "차량 편성 제거", description = "편성 해제")
    public ResponseEntity<Void> unassign(@PathVariable Long id, @PathVariable Long vehicleId) {
        service.unassignVehicle(id, vehicleId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/vehicles/{vehicleId}/status")
    @Operation(summary = "편성 차량 문자 상태", description = "0=PENDING,1=SENT,2=CLICKED,3=GPS_RECEIVED")
    public ResponseEntity<Void> updateVehicleStatus(@PathVariable Long id,
                                                    @PathVariable Long vehicleId,
                                                    @RequestBody UpdateVehicleStatusRequest req) {
        service.updateVehicleStatus(id, vehicleId, req.getStatusCode());
        return ResponseEntity.ok().build();
    }
}
