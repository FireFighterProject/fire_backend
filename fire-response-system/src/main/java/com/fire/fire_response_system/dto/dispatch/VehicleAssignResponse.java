package com.fire.fire_response_system.dto.dispatch;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class VehicleAssignResponse {

    private Long orderId;
    private Integer batchNo;
    private List<VehicleSummary> vehicles; // 차량 전체 정보 반환
}