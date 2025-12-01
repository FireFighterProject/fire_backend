package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.DispatchOrder;
import com.fire.fire_response_system.domain.dispatch.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DispatchOrderRepository extends JpaRepository<DispatchOrder, Long> {

    List<DispatchOrder> findAllByOrderByCreatedAtDesc();

    Optional<DispatchOrder> findTopByAddressAndStatusOrderByCreatedAtDesc(
            String address,
            DispatchStatus status
    );
}
