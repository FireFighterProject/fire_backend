package com.fire.fire_response_system.dto.dispatch;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DispatchOrderSummary {
    private Long orderId;
    private String title;
    private String address;
    private String content;
    private String status;
    private int batchCount;
    private LocalDateTime createdAt;
}
