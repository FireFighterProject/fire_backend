package com.fire.fire_response_system.dto.dispatch;

import lombok.Getter;
import java.util.List;

@Getter
public class VehicleAssignRequest {

    private List<Long> vehicleIds;  // 차량 ID 리스트만 받음
}