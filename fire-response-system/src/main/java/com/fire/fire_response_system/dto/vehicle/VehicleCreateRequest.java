package com.fire.fire_response_system.dto.vehicle;

import jakarta.validation.constraints.*;

import lombok.Getter;

@Getter
public class VehicleCreateRequest {
    @NotNull(message = "stationId 필수")
    private Long stationId;

    @NotBlank
    private String sido;

    @NotBlank(message = "callSign 필수")
    private String callSign;

    private String typeName;
    private Integer capacity;
    private Integer personnel;
    private String avlNumber;
    private String psLteNumber;

    /**
     * 0=대기, 1=활동, 2=철수 (기본 0)
     */
    private Integer status;

    /**
     * 0/1 (기본 0)
     */
    private Integer rallyPoint;
}
