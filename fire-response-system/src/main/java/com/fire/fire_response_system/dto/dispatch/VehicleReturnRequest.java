package com.fire.fire_response_system.dto.dispatch;

import lombok.Getter;

import java.util.List;

@Getter
public class VehicleReturnRequest {
    /** 복귀 처리할 차량 ID 목록 */
    private List<Long> vehicleIds;
}
