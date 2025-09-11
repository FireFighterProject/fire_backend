package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByStationIdAndCallSign(Long stationId, String callSign);

    // 수정 시 자기 자신은 제외하고 중복 체크
    boolean existsByStationIdAndCallSignAndIdNot(Long stationId, String callSign, Long id);

    @Query("select distinct v.typeName from Vehicle v where v.typeName is not null order by v.typeName asc")
    List<String> findDistinctTypeNames();
}
