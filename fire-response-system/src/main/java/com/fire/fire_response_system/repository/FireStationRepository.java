package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.FireStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FireStationRepository extends JpaRepository<FireStation, Long> {
    List<FireStation> findBySido(String sido);
}
