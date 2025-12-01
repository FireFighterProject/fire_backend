package com.fire.fire_response_system.dto.dispatch;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class VehicleCurrentDispatchResponse {

    private Long orderId;
    private String address;
    private String content;
    private String message;

    public static VehicleCurrentDispatchResponse notAssigned() {
        return VehicleCurrentDispatchResponse.builder()
                .orderId(null)
                .address(null)
                .content(null)
                .message("출동 상태가 아닙니다.")
                .build();
    }

    public static VehicleCurrentDispatchResponse of(Long orderId, String address, String content) {
        return VehicleCurrentDispatchResponse.builder()
                .orderId(orderId)
                .address(address)
                .content(content)
                .message("현재 출동 중입니다.")
                .build();
    }
}
