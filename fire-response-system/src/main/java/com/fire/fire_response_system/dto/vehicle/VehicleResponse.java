package com.fire.fire_response_system.dto.vehicle;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor
public class VehicleResponse {
    private Long id;
    private Long stationId;
    private String typeName;
    private String callSign;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;
    private Integer status;
    private Integer rallyPoint;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
