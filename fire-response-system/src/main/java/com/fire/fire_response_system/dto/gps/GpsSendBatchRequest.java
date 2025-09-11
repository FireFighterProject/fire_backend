package com.fire.fire_response_system.dto.gps;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;

@Getter
public class GpsSendBatchRequest {
    @NotNull
    private List<GpsSendRequest> items;
}
