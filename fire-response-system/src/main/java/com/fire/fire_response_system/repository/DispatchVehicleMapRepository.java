package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.DispatchStatus;
import com.fire.fire_response_system.domain.dispatch.DispatchVehicleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DispatchVehicleMapRepository extends JpaRepository<DispatchVehicleMap, Long> {

    boolean existsByAssignmentIdAndVehicleId(Long assignmentId, Long vehicleId);

    boolean existsByAssignmentId(Long assignmentId);

    List<DispatchVehicleMap> findByAssignmentId(Long assignmentId);

    @Query("""
        SELECT m FROM DispatchVehicleMap m
        JOIN m.assignment a
        JOIN a.order o
        WHERE m.vehicle.id = :vehicleId
        AND o.status != :endedStatus
        ORDER BY m.id DESC
    """)
    List<DispatchVehicleMap> findActiveMaps(
            @Param("vehicleId") Long vehicleId,
            @Param("endedStatus") DispatchStatus endedStatus
    );

    void deleteByVehicleId(Long vehicleId);

}
