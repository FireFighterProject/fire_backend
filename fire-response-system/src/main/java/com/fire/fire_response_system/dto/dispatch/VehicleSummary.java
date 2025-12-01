package com.fire.fire_response_system.dto.dispatch;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class VehicleSummary {

    private Long id;
    private Long stationId;
    private String sido;
    private String typeName;
    private String callSign;
    private Integer status;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;
}
