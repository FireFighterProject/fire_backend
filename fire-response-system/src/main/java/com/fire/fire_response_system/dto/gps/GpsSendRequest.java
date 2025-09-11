package com.fire.fire_response_system.dto.gps;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GpsSendRequest {
    @NotNull private Long vehicleId;
    @NotNull private Double latitude;
    @NotNull private Double longitude;
    private Double heading;
    private Double speedKph;
    // 장비에서 찍은 시각(없으면 서버 now)
    private LocalDateTime sentAt;
}
