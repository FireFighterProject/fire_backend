package com.fire.fire_response_system.dto.gps;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public class GpsSendBatchResponse {
    private int success;
    private int failed;
}
