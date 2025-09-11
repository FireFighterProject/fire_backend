package com.fire.fire_response_system.dto.dispatch;

import com.fire.fire_response_system.domain.dispatch.DispatchStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DispatchOrderItem {
    private Long id;
    private Long stationId;
    private String title;
    private String description;
    private DispatchStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
