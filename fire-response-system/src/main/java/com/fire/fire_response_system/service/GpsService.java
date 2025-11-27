package com.fire.fire_response_system.service;

import com.fire.fire_response_system.dto.gps.GpsSendRequest;
import com.fire.fire_response_system.dto.gps.GpsSendBatchRequest;
import com.fire.fire_response_system.dto.gps.MapStatsRequest;
import com.fire.fire_response_system.repository.VehicleLocationRepository;
import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class GpsService {

    private final VehicleRepository vehicleRepository;
    private final VehicleLocationRepository locationRepository;

    public void receive(GpsSendRequest req) {
        // TODO: vehicle_gps_log + vehicle_location 저장
    }

    public int receiveAll(GpsSendBatchRequest req) {
        return 0;
    }

    public Object logs(Long vehicleId,
                       LocalDateTime from,
                       LocalDateTime to,
                       int page,
                       int size) {
        return Collections.emptyList();
    }

    public Object status(Long stationId, Integer withinMinutes) {
        return Collections.emptyList();
    }

    /**
     * 🔥 stationId만 입력하면
     * → 그 소방서에 속한 모든 차량들의 최신 GPS 정보 조회
     */
    public Object lastLocationsAll(Long stationId) {

        // 1) stationId → vehicleId list 조회
        List<Long> vehicleIds = vehicleRepository.findIdsByStationId(stationId);
        if (vehicleIds.isEmpty())
            return List.of();

        // 2) 각 차량 최신 GPS 조회
        List<Map<String, Object>> result = new ArrayList<>();

        for (Long vid : vehicleIds) {

            var opt = locationRepository.findTop1ByVehicleIdOrderByLastUpdatedAtDesc(vid);

            if (opt.isPresent()) {
                var loc = opt.get();

                result.add(Map.of(
                        "vehicleId", vid,
                        "latitude", loc.getLatitude(),
                        "longitude", loc.getLongitude(),
                        "heading", loc.getHeading(),
                        "speedKph", loc.getSpeedKph(),
                        "lastUpdatedAt", loc.getLastUpdatedAt()
                ));

            } else {
                result.add(Map.of(
                        "vehicleId", vid,
                        "gps", "NO_DATA"
                ));
            }
        }

        return result;
    }

    public Object lastLocations(Long stationId, List<Long> vehicleIds) {
        return Collections.emptyList();
    }

    public Object mapStats(MapStatsRequest req) {
        return Map.of(
                "totalVehicles", 0,
                "activeVehicles", 0,
                "logs", Collections.emptyList()
        );
    }
}
