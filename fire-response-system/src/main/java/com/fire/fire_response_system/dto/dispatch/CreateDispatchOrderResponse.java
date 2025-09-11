package com.fire.fire_response_system.dto.dispatch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateDispatchOrderResponse {
    private Long dispatchOrderId;
    private String message;
}
