package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.DispatchVehicleMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispatchVehicleMapRepository extends JpaRepository<DispatchVehicleMap, Long> {

    boolean existsByAssignmentIdAndVehicleId(Long assignmentId, Long vehicleId);

    boolean existsByAssignmentId(Long assignmentId);

    List<DispatchVehicleMap> findByAssignmentId(Long assignmentId);

    Optional<DispatchVehicleMap> findByVehicleId(Long vehicleId);

    void deleteByVehicleId(Long vehicleId);

}
