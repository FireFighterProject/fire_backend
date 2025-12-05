package com.fire.fire_response_system.dto.vehicle;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class VehicleBatchResponse {

    private int total;          // 전체 요청 수
    private int inserted;       // 실제 등록된 수
    private int duplicates;     // callSign 중복 수
    private List<String> messages; // 스킵/오류 메시지 목록

    // 새로 추가: 이번 batch에서 실제 생성된 차량 PK 목록
    private List<Long> vehicleIds;

    public static VehicleBatchResponse empty() {
        return VehicleBatchResponse.builder()
                .messages(new ArrayList<>())
                .vehicleIds(new ArrayList<>())   // 추가된 부분
                .build();
    }
}
