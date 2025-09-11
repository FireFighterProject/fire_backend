package com.fire.fire_response_system.dto.dispatch;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateDispatchOrderRequest {
    private Long stationId;     // 선택: 사전 등록 검증 안 함
    private String title;       // 필수
    private String description; // 선택
}
