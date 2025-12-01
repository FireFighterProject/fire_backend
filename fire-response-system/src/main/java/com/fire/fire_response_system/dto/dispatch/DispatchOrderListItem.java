package com.fire.fire_response_system.dto.dispatch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DispatchOrderListItem {

    private Long orderId;
    private String title;
    private String address;
    private String content;
    private String status;

    private List<AssignedVehicleItem> vehicles;

    @Getter
    @AllArgsConstructor
    public static class AssignedVehicleItem {
        private Long vehicleId;
        private String callSign;
    }
}
