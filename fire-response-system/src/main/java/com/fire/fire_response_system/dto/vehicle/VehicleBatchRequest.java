package com.fire.fire_response_system.dto.vehicle;

import lombok.Getter;

@Getter
public class VehicleBatchRequest {
    private Long stationId;
    private String sido;        // ← 수정됨
    private String typeName;
    private String callSign;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;
}

