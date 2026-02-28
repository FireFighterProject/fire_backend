package com.fire.fire_response_system.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VehicleBatchDeleteResponse {
    private int requested;
    private int deleted;
}
