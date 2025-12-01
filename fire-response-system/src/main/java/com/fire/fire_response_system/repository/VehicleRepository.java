package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    boolean existsByStationIdAndCallSign(Long stationId, String callSign);

    boolean existsByStationIdAndCallSignAndIdNot(Long stationId, String callSign, Long id);

    @Query("select distinct v.typeName from Vehicle v where v.typeName is not null order by v.typeName asc")
    List<String> findDistinctTypeNames();

    // 🔥 stationId로 차량 ID만 조회
    @Query("select v.id from Vehicle v where v.stationId = :stationId")
    List<Long> findIdsByStationId(@Param("stationId") Long stationId);

    @Query("SELECT COALESCE(SUM(v.personnel), 0) FROM Vehicle v")
    int sumPersonnel();
}
