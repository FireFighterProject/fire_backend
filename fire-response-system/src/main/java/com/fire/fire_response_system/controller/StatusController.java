package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.status.StatusSummaryResponse;
import com.fire.fire_response_system.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "현황 집계")
@RestController
@RequestMapping("/api/status")
@RequiredArgsConstructor
public class StatusController {

    private final StatusService statusService;

    @Operation(summary = "현황 집계 조회 (mode=NORMAL/ DISASTER)")
    @GetMapping
    public StatusSummaryResponse getSummary(@RequestParam(defaultValue = "NORMAL") String mode) {
        return statusService.getSummary(mode);
    }
}
