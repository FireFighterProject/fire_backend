package com.fire.fire_response_system.dto.dispatch;

import lombok.Getter;

@Getter
public class CreateDispatchOrderRequest {

    private String title;
    private String address;
    private String content;
}
