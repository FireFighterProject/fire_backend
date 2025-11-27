package com.fire.fire_response_system.dto.vehicle;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class VehicleCreateRequest {

    /** 🔥 소방서 이름 (DB 컬럼: stations.name) */
    @NotBlank(message = "stationName 필수")
    private String stationName;

    /** 🔥 시도 (DB 컬럼: stations.sido) */
    @NotBlank(message = "sido 필수")
    private String sido;

    /** 🔥 호출명 */
    @NotBlank(message = "callSign 필수")
    private String callSign;

    /** 선택값들 */
    private String typeName;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;

    /** 0=대기, 1=활동, 2=철수 (기본 0) */
    private Integer status;

    /** 0/1 (기본 0, 또는 규칙 자동 적용) */
    private Integer rallyPoint;
}
