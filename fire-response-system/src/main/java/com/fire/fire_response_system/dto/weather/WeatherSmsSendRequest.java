package com.fire.fire_response_system.dto.weather;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WeatherSmsSendRequest {
    private Long vehicleId;
    private int nx;
    private int ny;
}
