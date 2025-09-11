package com.fire.fire_response_system.dto.vehicle;

import lombok.Getter;

@Getter
public class VehicleAssemblyUpdateRequest {
    /**
     * 0/1 (값이 없으면 토글)
     */
    private Integer rallyPoint;
}
