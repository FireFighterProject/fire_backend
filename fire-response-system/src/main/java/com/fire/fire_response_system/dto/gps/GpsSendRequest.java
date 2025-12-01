package com.fire.fire_response_system.dto.gps;

import lombok.Data;

@Data
public class GpsSendRequest {

    private Long vehicleId;   // 소방차 ID
    private Double latitude;  // 위도
    private Double longitude; // 경도
}
