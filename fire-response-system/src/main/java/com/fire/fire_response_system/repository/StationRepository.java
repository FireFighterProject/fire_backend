package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StationRepository extends JpaRepository<Station, Long> {
    boolean existsBySidoAndName(String sido, String name);
    boolean existsByName(String name); // sido 없이도 중복 검사할 때 사용
    // 시도별 목록 조회
    List<Station> findBySido(String sido);
}
