package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.dispatch.*;
import com.fire.fire_response_system.service.DispatchOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "출동명령 / 배치 편성 API",
        description = "출동명령 생성, 배치 자동 생성, 차량 편성, 복귀, 조회 기능 제공"
)
@RestController
@RequestMapping("/api/dispatch-orders")
@RequiredArgsConstructor
public class DispatchOrderController {

    private final DispatchOrderService dispatchOrderService;

    @PostMapping
    @Operation(summary = "출동명령 생성", description = "같은 주소·진행중(DRAFT)이면 기존 출동명령을 재사용합니다.")
    public ResponseEntity<DispatchOrderResponse> createOrder(
            @RequestBody CreateDispatchOrderRequest req
    ) {
        return ResponseEntity.ok(dispatchOrderService.createOrder(req));
    }

    @PostMapping("/{orderId}/assign")
    @Operation(summary = "차량 편성", description = "vehicleIds만 보내면 서버가 자동으로 배치 번호를 결정합니다.")
    public ResponseEntity<VehicleAssignResponse> assignVehicles(
            @PathVariable Long orderId,
            @RequestBody VehicleAssignRequest req
    ) {
        return ResponseEntity.ok(dispatchOrderService.assignVehicles(orderId, req));
    }

    @PostMapping("/{orderId}/return")
    @Operation(summary = "차량 복귀", description = "차량 상태를 0(대기)로 변경합니다.")
    public ResponseEntity<String> returnVehicles(
            @PathVariable Long orderId,
            @RequestBody VehicleReturnRequest req
    ) {
        dispatchOrderService.returnVehicles(orderId, req);
        return ResponseEntity.ok("복귀 완료");
    }

    @GetMapping
    @Operation(
            summary = "출동명령 목록 조회",
            description = "출동명령 목록과, 해당 명령에 배치된 모든 소방차의 ID 및 callSign을 반환합니다."
    )
    public ResponseEntity<?> listOrders() {
        return ResponseEntity.ok(dispatchOrderService.listOrders());
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "출동명령 상세 조회")
    public ResponseEntity<?> getOrderDetail(@PathVariable Long orderId) {
        return ResponseEntity.ok(dispatchOrderService.getOrderDetail(orderId));
    }

    @GetMapping("/{orderId}/batches/{batchNo}")
    @Operation(summary = "배치 상세 조회")
    public ResponseEntity<?> getBatchDetail(
            @PathVariable Long orderId,
            @PathVariable Integer batchNo
    ) {
        return ResponseEntity.ok(dispatchOrderService.getBatchDetail(orderId, batchNo));
    }

    @GetMapping("/vehicle/{vehicleId}")
    @Operation(
            summary = "특정 차량의 출동 정보 조회",
            description = "차량이 현재 배치되어 있는 출동명령(order)을 조회합니다. 배치 중이 아니라면 '출동 상태가 아닙니다.'를 반환합니다."
    )
    public ResponseEntity<?> getCurrentDispatchByVehicle(
            @PathVariable Long vehicleId
    ) {
        var res = dispatchOrderService.getCurrentDispatchByVehicle(vehicleId);
        return ResponseEntity.ok(res);
    }

}
