package com.fire.fire_response_system.dto.vehicle;

import lombok.Getter;

@Getter
public class VehicleUpdateRequest {
    // stationId는 이동 기능이 아니라서 제외 (원하면 나중에 별도 API)
    private String callSign;     // 변경 시 중복검사(stationId 내)
    private String typeName;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;
}
