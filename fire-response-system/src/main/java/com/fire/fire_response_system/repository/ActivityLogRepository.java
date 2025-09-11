package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.activity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByVehicleIdOrderByStartedAtDesc(Long vehicleId);
}