package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Long> {
    boolean existsBySidoAndName(String sido, String name);
    boolean existsByName(String name); // sido 없이 쓰고 싶을 때
}
