package com.fire.fire_response_system.dto.weather;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DayForecast {
    private String date;
    private String minTemp;
    private String maxTemp;
    private String sky;
    private String pop;
    private String pty;
}
