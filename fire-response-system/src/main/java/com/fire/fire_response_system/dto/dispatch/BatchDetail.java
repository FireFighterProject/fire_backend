package com.fire.fire_response_system.dto.dispatch;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class BatchDetail {
    private Integer batchNo;
    private List<VehicleSummary> vehicles;
}
