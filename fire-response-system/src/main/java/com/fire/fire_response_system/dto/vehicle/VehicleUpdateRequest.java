package com.fire.fire_response_system.dto.vehicle;

import lombok.Getter;

@Getter
public class VehicleUpdateRequest {
    // stationId는 이동 기능이 아니라서 제외 (원하면 나중에 별도 API)
    private String callSign;
    private String typeName;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;
    private String phoneNumber;
}
