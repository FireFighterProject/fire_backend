package com.fire.fire_response_system.dto.gps;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class GpsLogItem {
    private Long id;
    private Long vehicleId;
    private Double latitude;
    private Double longitude;
    private Double heading;
    private Double speedKph;
    private LocalDateTime recordedAt;
}
