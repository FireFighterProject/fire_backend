package com.fire.fire_response_system.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class VehicleListItem {
    private Long id;
    private Long stationId;
    private String typeName;
    private String callSign;
    private Integer status;
    private Integer rallyPoint;
}
