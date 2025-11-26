package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.DispatchVehicleMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DispatchVehicleMapRepository extends JpaRepository<DispatchVehicleMap, Long> {

    // 출동 명령 기준 전체 매핑 조회
    List<DispatchVehicleMap> findByDispatchOrderId(Long dispatchOrderId);

    // 특정 출동 + 차량 매핑이 존재하는지 여부
    boolean existsByDispatchOrderIdAndVehicleId(Long dispatchOrderId, Long vehicleId);

    // 특정 출동 + 차량 매핑 한 건 조회
    Optional<DispatchVehicleMap> findByDispatchOrderIdAndVehicleId(Long dispatchOrderId, Long vehicleId);

    // 매핑 해제(복귀) 처리
    @Modifying
    @Query(
            value = """
                UPDATE dispatch_vehicle_map m
                   SET m.unassigned_at = :time
                 WHERE m.vehicle_id = :vehicleId
                   AND m.dispatch_order_id = :orderId
                   AND m.unassigned_at IS NULL
                """,
            nativeQuery = true
    )
    int closeOne(
            @Param("vehicleId") Long vehicleId,
            @Param("orderId") Long orderId,
            @Param("time") LocalDateTime time
    );
}
