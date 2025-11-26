package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.dispatch.DispatchBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DispatchBatchRepository extends JpaRepository<DispatchBatch, Long> { }
