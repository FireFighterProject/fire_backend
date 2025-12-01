package com.fire.fire_response_system.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StatsResponse {
    private int firefighterCount;  // 차량 personnel 합계
    private int activeStations;    // 소방서 수
    private int totalVehicles;     // 차량 수
}
