package com.fire.fire_response_system.dto.activity;

import lombok.Getter;

@Getter
public class ActivityStartRequest {
    private Long vehicleId;
    private Long stationId;
    private String place;
    private String description;
}