package com.fire.fire_response_system.dto.vehicle;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class VehicleStatusUpdateRequest {
    /**
     * 0=대기, 1=활동, 2=철수
     */
    @NotNull(message = "status 필수")
    private Integer status;
}
