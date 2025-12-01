package com.fire.fire_response_system.dto.dispatch;

import com.fire.fire_response_system.domain.dispatch.DispatchStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class DispatchOrderResponse {

    private Long id;
    private String title;
    private String address;
    private String content;
    private DispatchStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
