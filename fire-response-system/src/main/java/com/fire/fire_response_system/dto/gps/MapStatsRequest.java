package com.fire.fire_response_system.dto.gps;

import lombok.Getter;

@Getter
public class MapStatsRequest {
    private Double minLat;
    private Double maxLat;
    private Double minLng;
    private Double maxLng;
}