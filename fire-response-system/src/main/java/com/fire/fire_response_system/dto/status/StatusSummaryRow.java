package com.fire.fire_response_system.dto.status;

import lombok.*;

import java.util.Map;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class StatusSummaryRow {
    private String region;       // 지역명 (경북 전체 / 경북 대기 / 경북 활동 / 서울 전체 …)
    private int totalVehicles;   // 차량 수
    private int totalPersonnel;  // 인원 수
    private Map<String, Integer> typeCounts; // 차종별 카운트
}