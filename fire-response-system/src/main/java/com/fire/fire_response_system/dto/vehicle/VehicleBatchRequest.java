package com.fire.fire_response_system.dto.vehicle;

import lombok.Getter;

@Getter
public class VehicleBatchRequest {
//  private Long stationId;     // id대신 name으로 대체
    private String stationName;
    private String sido;        // ← 수정됨
    private String typeName;
    private String callSign;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;
}

