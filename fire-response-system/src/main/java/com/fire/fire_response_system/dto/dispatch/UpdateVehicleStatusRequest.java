package com.fire.fire_response_system.dto.dispatch;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateVehicleStatusRequest {
    // 0=PENDING, 1=SENT, 2=CLICKED, 3=GPS_RECEIVED
    private Integer statusCode;
}
