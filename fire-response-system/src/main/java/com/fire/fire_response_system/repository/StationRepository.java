package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    boolean existsBySidoAndName(String sido, String name);
    boolean existsByName(String name);
    List<Station> findBySido(String sido); // 지역별 조회
}
