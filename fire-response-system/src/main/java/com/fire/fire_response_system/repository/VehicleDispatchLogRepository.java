package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.VehicleDispatchLog;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleDispatchLogRepository extends JpaRepository<VehicleDispatchLog, Long> {

    @Query("""
        SELECT v FROM VehicleDispatchLog v
        WHERE v.vehicleId = :vehicleId
          AND v.dispatchOrderId = :orderId
          AND v.returnedAt IS NULL
        ORDER BY v.givenAt DESC
    """)
    Optional<VehicleDispatchLog> findLatestOpen(
            @Param("vehicleId") Long vehicleId,
            @Param("orderId") Long orderId
    );
}
