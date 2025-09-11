package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.DispatchVehicleMap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispatchVehicleMapRepository extends JpaRepository<DispatchVehicleMap, Long> {
    List<DispatchVehicleMap> findByDispatchOrderId(Long dispatchOrderId);
    boolean existsByDispatchOrderIdAndVehicleId(Long dispatchOrderId, Long vehicleId);
    Optional<DispatchVehicleMap> findByDispatchOrderIdAndVehicleId(Long dispatchOrderId, Long vehicleId);
    long countByDispatchOrderId(Long dispatchOrderId);
}
