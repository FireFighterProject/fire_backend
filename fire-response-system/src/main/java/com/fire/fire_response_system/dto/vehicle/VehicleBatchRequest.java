package com.fire.fire_response_system.dto.vehicle;

import lombok.Getter;

@Getter
public class VehicleBatchRequest {
    private String stationName;
    private String callSign;
    private String typeName;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;
    private String phoneNumber;
}

