package com.fire.fire_response_system.dto.gps;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class MapStatsResponse {
    private int totalVehicles;
    private int totalPersonnel;
    private Map<String, Integer> typeCounts;
    private List<MapVehicleItem> vehicles;

    @Getter @Setter @Builder
    @NoArgsConstructor @AllArgsConstructor
    public static class MapVehicleItem {
        private Long vehicleId;
        private String callSign;
        private String typeName;
        private Double lat;
        private Double lng;
        private Integer personnel;
    }
}