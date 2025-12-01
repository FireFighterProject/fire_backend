package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.DispatchAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispatchAssignmentRepository extends JpaRepository<DispatchAssignment, Long> {

    Optional<DispatchAssignment> findTopByOrderIdOrderByBatchNoDesc(Long orderId);

    int countByOrderId(Long orderId);

    List<DispatchAssignment> findByOrderIdOrderByBatchNoAsc(Long orderId);

    Optional<DispatchAssignment> findByOrderIdAndBatchNo(Long orderId, Integer batchNo);
}
