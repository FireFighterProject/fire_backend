package com.fire.fire_response_system.dto.activity;

import com.fire.fire_response_system.domain.activity.ActivityLog;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ActivityLogResponse {
    private Long id;
    private Long vehicleId;
    private Long stationId;
    private String place;
    private String description;
    private Integer status;
    private LocalDateTime startedAt;
    private LocalDateTime returnedAt;
    private LocalDateTime movedAt;

    public static ActivityLogResponse from(ActivityLog log) {
        return ActivityLogResponse.builder()
                .id(log.getId())
                .vehicleId(log.getVehicleId())
                .stationId(log.getStationId())
                .place(log.getPlace())
                .description(log.getDescription())
                .status(log.getStatus())
                .startedAt(log.getStartedAt())
                .returnedAt(log.getReturnedAt())
                .movedAt(log.getMovedAt())
                .build();
    }
}