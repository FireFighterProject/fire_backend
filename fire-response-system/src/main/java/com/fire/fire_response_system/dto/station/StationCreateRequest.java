package com.fire.fire_response_system.dto.station;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StationCreateRequest {
    private String sido;                 // 선택
    @NotBlank(message = "name 필수")
    private String name;                 // 필수
    private String address;              // 선택
}
