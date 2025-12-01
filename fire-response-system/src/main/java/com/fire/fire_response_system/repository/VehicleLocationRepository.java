package com.fire.fire_response_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VehicleLocationRepository extends JpaRepository<VehicleLocation, Long> {
    Optional<VehicleLocation> findByVehicleId(Long vehicleId);
    List<VehicleLocation> findByLastUpdatedAtGreaterThanEqual(LocalDateTime threshold);
    List<VehicleLocation> findByVehicleIdIn(Iterable<Long> ids);
    Optional<VehicleLocation> findTop1ByVehicleIdOrderByLastUpdatedAtDesc(Long vehicleId);
}
