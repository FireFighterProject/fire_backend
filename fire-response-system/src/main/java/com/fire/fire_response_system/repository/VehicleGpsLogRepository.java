package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.gps.VehicleGpsLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VehicleGpsLogRepository extends JpaRepository<VehicleGpsLog, Long> {

    // 특정 차량 GPS 로그 조회 + logMode 필터
    List<VehicleGpsLog> findByVehicleIdAndLogModeOrderByCapturedAtDesc(Long vehicleId, Integer logMode);

    // 기간 + 차량 + logMode 조회
    List<VehicleGpsLog> findByVehicleIdAndLogModeAndCapturedAtBetweenOrderByCapturedAtDesc(
            Long vehicleId, Integer logMode, LocalDateTime start, LocalDateTime end
    );

    // 최근 로그 확인
    @Query("SELECT g FROM VehicleGpsLog g WHERE g.vehicleId = :vehicleId ORDER BY g.id DESC")
    List<VehicleGpsLog> findLatest(Long vehicleId);
}
