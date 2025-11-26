package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.DispatchOrder;
import com.fire.fire_response_system.domain.dispatch.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DispatchOrderRepository extends JpaRepository<DispatchOrder, Long> {

    /** 상태별 조회 */
    List<DispatchOrder> findByStatus(DispatchStatus status);

    /** 소방서별 특정 상태(SENT/ACTIVE 등) 출동명령 존재 여부 */
    boolean existsByStationIdAndStatusIn(Long stationId, List<DispatchStatus> statuses);

    /** 차량이 현재 SENT 또는 ACTIVE 출동 중인지 체크 */
    @Query("""
        SELECT CASE WHEN COUNT(o) > 0 THEN true ELSE false END
        FROM DispatchOrder o
        JOIN DispatchVehicleMap m
          ON m.dispatchOrderId = o.id
        WHERE m.vehicleId = :vehicleId
          AND o.status IN (:sent, :active)
        """)
    boolean existsActiveOrderByVehicleId(
            @Param("vehicleId") Long vehicleId,
            @Param("sent") DispatchStatus sent,
            @Param("active") DispatchStatus active
    );
}