package com.fire.fire_response_system.dto.vehicle;

import lombok.Getter;

@Getter
public class VehicleBatchRequest {
    private Long stationId;      // 소방서 ID
    private String province;     // 시도 (경북/서울 등)
    private String typeName;     // 차종
    private String callSign;     // 호출명
    private Integer capacity;    // 용량
    private Integer personnel;   // 인원
    private String avlNumber;    // AVL 단말기 번호
    private String psLteNumber;  // PS-LTE 번호
}
