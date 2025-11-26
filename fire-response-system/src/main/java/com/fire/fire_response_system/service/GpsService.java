// src/main/java/com/fire/fire_response_system/service/GpsService.java
package com.fire.fire_response_system.service;

import com.fire.fire_response_system.dto.gps.GpsSendRequest;
import com.fire.fire_response_system.dto.gps.GpsSendBatchRequest;
import com.fire.fire_response_system.dto.gps.MapStatsRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GpsService {

    public void receive(GpsSendRequest req) {
        // TODO: vehicle_gps_log + vehicle_current_location 저장
    }

    public int receiveAll(GpsSendBatchRequest req) {
        // TODO: req.getItems().forEach(this::receive)
        return 0;
    }

    public Object logs(Long vehicleId,
                       LocalDateTime from,
                       LocalDateTime to,
                       int page,
                       int size) {
        // TODO: repository 조회 후 Paging
        return Collections.emptyList();
    }

    public Object status(Long stationId, Integer withinMinutes) {
        // TODO: 차량 마지막 위치 기반 수신 시간 체크
        return Collections.emptyList();
    }

    public Object lastLocations(Long stationId, List<Long> vehicleIds) {
        // TODO: stationId + vehicleIds 조건 조회
        return Collections.emptyList();
    }

    public Object mapStats(MapStatsRequest req) {
        // TODO: bbox 기반 차량 필터링, 상태통계 계산
        return Map.of(
                "totalVehicles", 0,
                "activeVehicles", 0,
                "logs", Collections.emptyList()
        );
    }
}
