package com.fire.fire_response_system.dto.gps;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class VehicleStatusItem {
    private Long vehicleId;
    private boolean online;
    private LocalDateTime updatedAt; // 최신 갱신 시각
}

