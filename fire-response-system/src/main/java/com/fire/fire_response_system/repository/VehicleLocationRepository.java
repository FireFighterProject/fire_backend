package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.gps.VehicleLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Long> {

    Optional<VehicleLocation> findByVehicleId(Long vehicleId);

    void deleteByVehicleId(Long vehicleId);
}
