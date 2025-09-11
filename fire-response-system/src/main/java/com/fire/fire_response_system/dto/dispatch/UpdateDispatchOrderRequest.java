package com.fire.fire_response_system.dto.dispatch;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UpdateDispatchOrderRequest {
    private String title;        // null 아니면 변경
    private String description;  // null 아니면 변경
}
