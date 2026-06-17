package com.fire.fire_response_system.dto.dispatch;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CreateDispatchOrderRequest {

    @NotBlank(message = "title 필수")
    private String title;

    @NotBlank(message = "address 필수")
    private String address;

    @NotBlank(message = "content 필수")
    private String content;
}
