package com.fire.fire_response_system.dto.vehicle;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class VehicleBatchResponse {
    private int total;
    private int inserted;
    private int duplicates;
    private List<String> messages;

    public static VehicleBatchResponse empty() {
        return VehicleBatchResponse.builder()
                .messages(new ArrayList<>())
                .build();
    }
}