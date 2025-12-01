package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.gps.VehicleGpsLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleGpsLogRepository extends JpaRepository<VehicleGpsLog, Long> {
}
