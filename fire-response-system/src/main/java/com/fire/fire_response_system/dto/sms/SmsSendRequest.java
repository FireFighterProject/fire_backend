package com.fire.fire_response_system.dto.sms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsSendRequest {
    private Long vehicleId;
    private String text;
}
