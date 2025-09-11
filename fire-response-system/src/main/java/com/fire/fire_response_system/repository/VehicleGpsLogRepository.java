package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.gps.VehicleGpsLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface VehicleGpsLogRepository extends JpaRepository<VehicleGpsLog, Long> {
    Page<VehicleGpsLog> findByVehicleIdOrderBySentAtDesc(Long vehicleId, Pageable pageable);
    Page<VehicleGpsLog> findByVehicleIdAndSentAtBetweenOrderBySentAtDesc(
            Long vehicleId, LocalDateTime from, LocalDateTime to, Pageable pageable);
}
