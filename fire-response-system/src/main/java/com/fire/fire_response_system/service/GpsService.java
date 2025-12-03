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
     * - 기존 위치가 있으면 기존 위치를 가리켜서 gps_log에 기록
     * - vehicle_location은 최신 값으로 갱신
     * - gps_log에는 "이전값" + "신규값 모두 기록"이 아니라 "이전값만 기록"
     *   (필요하면 둘 다 기록하는 버전도 만들어줄 수 있음)
     */
    @Transactional
    public void receive(GpsSendRequest req) {

        if (req.getVehicleId() == null)
            throw new IllegalArgumentException("vehicleId는 필수입니다.");

        if (req.getLatitude() == null || req.getLongitude() == null)
            throw new IllegalArgumentException("위도/경도는 필수입니다.");

        Long vehicleId = req.getVehicleId();
        LocalDateTime now = LocalDateTime.now();

        // 1) 👉 기존 위치 조회
        VehicleLocation exist = locationRepo.findByVehicleId(vehicleId).orElse(null);

        if (exist != null) {
            // 2) 👉 기존 좌표를 gps_log에 백업
            VehicleGpsLog oldLog = VehicleGpsLog.builder()
                    .vehicleId(vehicleId)
                    .latitude(exist.getLatitude())
                    .longitude(exist.getLongitude())
                    .loggedAt(exist.getLastUpdatedAt())
                    .build();

            logRepo.save(oldLog);
        }

        // 3) 👉 최신 좌표 location에 upsert
        VehicleLocation loc = (exist == null)
                ? VehicleLocation.builder().vehicleId(vehicleId).build()
                : exist;

        loc.setLatitude(req.getLatitude());
        loc.setLongitude(req.getLongitude());
        loc.setLastUpdatedAt(now);

        locationRepo.save(loc);
    }

    /**
     * 특정 차량 현재 위치 조회
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
                        .build())
                .orElse(
                        VehicleLocationResponse.builder()
                                .vehicleId(vehicleId)
                                .message("GPS 정보 없음")
                                .build()
                );
    }

    /**
     * 소방서 소속 차량 현재 위치 조회
     */
    @Transactional(readOnly = true)
    public List<VehicleLocationResponse> getStationLocations(Long stationId) {

        TypedQuery<Vehicle> q = em.createQuery(
                "SELECT v FROM Vehicle v WHERE v.stationId = :sid", Vehicle.class);
        q.setParameter("sid", stationId);

        List<Vehicle> vehicles = q.getResultList();
        List<VehicleLocationResponse> result = new ArrayList<>();

        for (Vehicle v : vehicles) {
            VehicleLocation loc = locationRepo.findByVehicleId(v.getId()).orElse(null);

            if (loc == null) {
                result.add(VehicleLocationResponse.builder()
                        .vehicleId(v.getId())
                        .message("GPS 정보 없음")
                        .build());
            } else {
                result.add(VehicleLocationResponse.builder()
                        .vehicleId(v.getId())
                        .latitude(loc.getLatitude())
                        .longitude(loc.getLongitude())
                        .updatedAt(loc.getLastUpdatedAt())
                        .build());
            }
        }

        return result;
    }

    /**
     * GPS가 등록된 모든 차량
     */
    @Transactional(readOnly = true)
    public List<VehicleLocationResponse> getAll() {

        return locationRepo.findAll().stream()
                .map(loc -> VehicleLocationResponse.builder()
                        .vehicleId(loc.getVehicleId())
                        .latitude(loc.getLatitude())
                        .longitude(loc.getLongitude())
                        .updatedAt(loc.getLastUpdatedAt())
                        .build())
                .toList();
    }
}
