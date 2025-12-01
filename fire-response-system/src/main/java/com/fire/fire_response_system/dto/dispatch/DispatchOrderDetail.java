package com.fire.fire_response_system.dto.dispatch;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DispatchOrderDetail {

    private Long orderId;
    private String title;
    private String address;
    private String content;
    private String status;
    private List<BatchDetail> batches;
}
