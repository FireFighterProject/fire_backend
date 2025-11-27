package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.station.Station;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StationRepository extends JpaRepository<Station, Long> {

    // sido + name 중복 체크
    boolean existsBySidoAndName(String sido, String name);

    // sido + name 단건 조회
    Optional<Station> findBySidoAndName(String sido, String name);

    // 지역별 조회
    List<Station> findBySido(String sido);
}
