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

    /**
     * 단건 GPS 수신
     * 현재는 아직 로그 저장 로직을 구현하지 않고,
     * 컴파일 에러 해소용으로만 비워 둔다.
     */
    public void receive(GpsSendRequest req) {
        // TODO: vehicle_gps_log / vehicle_location 저장 로직 넣기
    }

    /**
     * 배치 GPS 수신
     * @return 처리된 건수(지금은 0 리턴)
     */
    public int receiveAll(GpsSendBatchRequest req) {
        // TODO: req 안의 리스트를 순회하면서 receive() 호출 등 구현
        return 0;
    }

    /**
     * 차량별 GPS 로그 조회
     */
    public Object logs(Long vehicleId,
                       LocalDateTime from,
                       LocalDateTime to,
                       int page,
                       int size) {
        // TODO: vehicle_gps_log 에서 조회하는 로직 구현
        return Collections.emptyList();
    }

    /**
     * 소방서별 차량 GPS 수신 상태 조회
     */
    public Object status(Long stationId, Integer withinMinutes) {
        // TODO: vehicle_location 기준으로 withinMinutes 이내 수신 여부 계산
        return Collections.emptyList();
    }

    /**
     * 소방서별 / 특정 차량들 마지막 위치 조회
     */
    public Object lastLocations(Long stationId, List<Long> vehicleIds) {
        // TODO: vehicle_location 에서 stationId, vehicleIds 로 조회
        return Collections.emptyList();
    }

    /**
     * 지도 박스 영역 내 통계
     */
    public Object mapStats(MapStatsRequest req) {
        // TODO: req 의 bbox/zoom 등을 이용해서 통계 계산
        return Map.of(
                "totalVehicles", 0,
                "activeVehicles", 0,
                "logs", Collections.emptyList()
        );
    }
}
