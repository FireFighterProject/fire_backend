package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // --- 기존 서비스에서 사용하던 메서드들 ---

    // 같은 소방서(station) 안에서 콜사인(callSign) 중복 확인
    boolean existsByStationIdAndCallSign(Long stationId, String callSign);

    // 수정 시 자기 자신(id)은 제외하고 콜사인 중복 확인
    boolean existsByStationIdAndCallSignAndIdNot(Long stationId, String callSign, Long id);

    // 차량 타입명(typeName) 목록(중복 제거)
    @Query("select distinct v.typeName from Vehicle v where v.typeName is not null order by v.typeName asc")
    List<String> findDistinctTypeNames();


    // --- 우리가 새로 추가한 출동 카운트/배치용 메서드들 ---

    // 여러 ID 한 번에 조회
    List<Vehicle> findByIdIn(List<Long> ids);

    // 출동 횟수(dispatchCount) 일괄 +1
    @Modifying
    @Query("update Vehicle v set v.dispatchCount = v.dispatchCount + 1 where v.id in :ids")
    int incrementDispatchCount(@Param("ids") List<Long> ids);
}
