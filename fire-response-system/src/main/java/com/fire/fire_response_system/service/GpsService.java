package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.gps.VehicleGpsLog;
import com.fire.fire_response_system.domain.gps.VehicleLocation;
import com.fire.fire_response_system.domain.vehicle.Vehicle;
import com.fire.fire_response_system.dto.gps.GpsSendRequest;
import com.fire.fire_response_system.dto.gps.VehicleLocationResponse;
import com.fire.fire_response_system.repository.VehicleGpsLogRepository;
import com.fire.fire_response_system.repository.VehicleLocationRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GpsService {

    private final VehicleLocationRepository locationRepo;
    private final VehicleGpsLogRepository logRepo;
    private final EntityManager em;   // 소방서별 차량 조회용

    /**
     * GPS 단건 수신
     * - vehicle_location: upsert (없으면 생성, 있으면 업데이트)
     * - vehicle_gps_log: 항상 이력 기록 남김
     */
    @Transactional
    public void receive(GpsSendRequest req) {

        if (req.getVehicleId() == null) {
            throw new IllegalArgumentException("vehicleId는 필수입니다.");
        }
        if (req.getLatitude() == null || req.getLongitude() == null) {
            throw new IllegalArgumentException("위도/경도는 필수입니다.");
        }

        // 1) 현재 위치 upsert
        VehicleLocation loc = locationRepo.findByVehicleId(req.getVehicleId())
                .orElseGet(() -> VehicleLocation.builder()
                        .vehicleId(req.getVehicleId())
                        .build()
                );

        loc.setLatitude(req.getLatitude());
        loc.setLongitude(req.getLongitude());
        loc.setLastUpdatedAt(LocalDateTime.now());

        locationRepo.save(loc);

        // 2) 이력 로그 저장
        VehicleGpsLog log = VehicleGpsLog.builder()
                .vehicleId(req.getVehicleId())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .build();

        logRepo.save(log);
    }

    /**
     * 특정 차량의 현재 위치 조회
     * - 위치가 없으면 "GPS 정보 없음" 메시지 반환
     */
    @Transactional(readOnly = true)
    public VehicleLocationResponse getLocation(Long vehicleId) {

        return locationRepo.findByVehicleId(vehicleId)
                .map(loc -> VehicleLocationResponse.builder()
                        .vehicleId(vehicleId)
                        .latitude(loc.getLatitude())
                        .longitude(loc.getLongitude())
                        .updatedAt(loc.getLastUpdatedAt())
                        .message(null)
                        .build()
                )
                .orElse(
                        VehicleLocationResponse.builder()
                                .vehicleId(vehicleId)
                                .latitude(null)
                                .longitude(null)
                                .updatedAt(null)
                                .message("GPS 정보 없음")
                                .build()
                );
    }

    /**
     * 특정 소방서(stationId)에 소속된 모든 차량의 위치 조회
     * - 차량 목록: vehicles.stationId = :stationId
     * - 각 차량별로 vehicle_location 조회
     * - 위도/경도가 없으면 "GPS 정보 없음" 메시지 포함해서 반환
     */
    @Transactional(readOnly = true)
    public List<VehicleLocationResponse> getStationLocations(Long stationId) {

        // 1) 해당 소방서의 차량 목록 조회
        TypedQuery<Vehicle> query = em.createQuery(
                "SELECT v FROM Vehicle v WHERE v.stationId = :stationId",
                Vehicle.class
        );
        query.setParameter("stationId", stationId);

        List<Vehicle> vehicles = query.getResultList();
        List<VehicleLocationResponse> result = new ArrayList<>();

        // 2) 각 차량별로 현재 위치 붙이기
        for (Vehicle v : vehicles) {
            VehicleLocation loc = locationRepo.findByVehicleId(v.getId())
                    .orElse(null);

            if (loc == null || loc.getLatitude() == null || loc.getLongitude() == null) {
                // GPS 정보 없음
                result.add(
                        VehicleLocationResponse.builder()
                                .vehicleId(v.getId())
                                .latitude(null)
                                .longitude(null)
                                .updatedAt(null)
                                .message("GPS 정보 없음")
                                .build()
                );
            } else {
                result.add(
                        VehicleLocationResponse.builder()
                                .vehicleId(v.getId())
                                .latitude(loc.getLatitude())
                                .longitude(loc.getLongitude())
                                .updatedAt(loc.getLastUpdatedAt())
                                .message(null)
                                .build()
                );
            }
        }

        return result;
    }

    /**
     * GPS가 등록된 모든 차량 위치 목록
     * - vehicle_location 에 존재하는 vehicleId만 반환
     * - GPS 등록 안 된 차량은 애초에 결과에 포함되지 않음
     */
    @Transactional(readOnly = true)
    public List<VehicleLocationResponse> getAll() {

        List<VehicleLocation> locations = locationRepo.findAll();

        return locations.stream()
                .map(loc -> VehicleLocationResponse.builder()
                        .vehicleId(loc.getVehicleId())
                        .latitude(loc.getLatitude())
                        .longitude(loc.getLongitude())
                        .updatedAt(loc.getLastUpdatedAt())
                        .message(null)
                        .build()
                )
                .toList();
    }
}
