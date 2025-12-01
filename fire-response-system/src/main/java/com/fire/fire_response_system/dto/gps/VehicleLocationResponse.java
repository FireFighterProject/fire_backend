package com.fire.fire_response_system.dto.gps;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class VehicleLocationResponse {

    private Long vehicleId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime updatedAt;

    private String message; // GPS 정보 없을 때 "GPS 정보 없음"
}
