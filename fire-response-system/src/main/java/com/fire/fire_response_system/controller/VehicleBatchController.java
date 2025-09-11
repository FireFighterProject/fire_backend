package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.vehicle.VehicleBatchRequest;
import com.fire.fire_response_system.dto.vehicle.VehicleBatchResponse;
import com.fire.fire_response_system.service.VehicleBatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "차량 관리 - 일괄 등록")
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleBatchController {

    private final VehicleBatchService batchService;

    @Operation(summary = "차량 다건 등록 (엑셀→JSON 변환)")
    @PostMapping("/batch")
    public VehicleBatchResponse registerBatch(@RequestBody List<VehicleBatchRequest> requests) {
        return batchService.register(requests);
    }
}