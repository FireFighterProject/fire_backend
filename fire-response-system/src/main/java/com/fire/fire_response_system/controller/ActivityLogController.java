package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.activity.*;
import com.fire.fire_response_system.dto.common.MessageResponse;
import com.fire.fire_response_system.service.ActivityLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "활동 기록")
@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    @Operation(summary = "활동 시작 기록")
    @PostMapping("/start")
    public Long start(@RequestBody ActivityStartRequest req) {
        return activityLogService.start(req);
    }

    @Operation(summary = "복귀 처리")
    @PatchMapping("/{id}/return")
    public MessageResponse returnVehicle(@PathVariable Long id) {
        return activityLogService.returnVehicle(id);
    }

    @Operation(summary = "장소 이동 처리")
    @PostMapping("/{id}/move")
    public MessageResponse move(@PathVariable Long id, @RequestBody ActivityMoveRequest req) {
        return activityLogService.move(id, req);
    }

    @Operation(summary = "활동 이력 조회")
    @GetMapping
    public List<ActivityLogResponse> list() {
        return activityLogService.list();
    }

    @Operation(summary = "활동 상세 지도 팝업")
    @GetMapping("/{id}/map")
    public ActivityLogResponse mapDetail(@PathVariable Long id) {
        return activityLogService.detail(id);
    }
}