package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.common.MessageResponse;
import com.fire.fire_response_system.service.VehicleBulkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "차량 관리 - 일괄 처리")
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleBulkController {

    private final VehicleBulkService bulkService;

    @Operation(summary = "전체 차량 GPS 수신 요청")
    @PostMapping("/gps-request-all")
    public MessageResponse gpsRequestAll() {
        return bulkService.requestGpsAll();
    }

    @Operation(summary = "전체 차량 철수 처리")
    @PatchMapping("/retire-all")
    public MessageResponse retireAll() {
        return bulkService.retireAll();
    }
}