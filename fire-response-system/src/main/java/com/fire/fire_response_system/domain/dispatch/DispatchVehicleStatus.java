package com.fire.fire_response_system.domain.dispatch;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DispatchVehicleStatus {
    PENDING(0),       // 대기(문자 미발송 or 대기)
    SENT(1),          // 문자 발송됨
    CLICKED(2),       // 문자 클릭
    GPS_RECEIVED(3);  // GPS 수신

    private final int code;

    public static DispatchVehicleStatus from(int code) {
        for (DispatchVehicleStatus s : values()) if (s.code == code) return s;
        throw new IllegalArgumentException("Invalid DispatchVehicleStatus code: " + code);
    }
}
