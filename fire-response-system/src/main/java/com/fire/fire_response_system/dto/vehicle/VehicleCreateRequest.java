package com.fire.fire_response_system.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VehicleCreateRequest {

    @NotBlank(message = "stationName 필수")
    private String stationName;

    @NotBlank(message = "callSign 필수")
    private String callSign;

    private String typeName;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;
    private String phoneNumber;

    private Integer status;
    private Integer rallyPoint;
}
