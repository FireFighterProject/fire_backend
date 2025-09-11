package com.fire.fire_response_system.dto.gps;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class VehicleLocationItem {
    private Long vehicleId;
    private Double latitude;
    private Double longitude;
    private Double heading;
    private Double speedKph;
}
