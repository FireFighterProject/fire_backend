package com.fire.fire_response_system.repository;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // ===== 중복 검사 (삭제 안 된 차량 기준) =====
    boolean existsByStationIdAndCallSignAndDeletedAtIsNull(Long stationId, String callSign);

    boolean existsByStationIdAndCallSignAndIdNotAndDeletedAtIsNull(
            Long stationId, String callSign, Long id
    );

    // ===== 차종 목록 (삭제 안 된 차량만) =====
    @Query("""
        select distinct v.typeName
        from Vehicle v
        where v.typeName is not null
          and v.deletedAt is null
        order by v.typeName asc
    """)
    List<String> findDistinctTypeNames();

    // ===== 소방서별 차량 ID (삭제 안 된 차량만) =====
    @Query("""
        select v.id
        from Vehicle v
        where v.stationId = :stationId
          and v.deletedAt is null
    """)
    List<Long> findIdsByStationId(@Param("stationId") Long stationId);

    // ===== Soft Delete용 =====
    boolean existsByIdAndDeletedAtIsNull(Long id);

    long countByIdInAndDeletedAtIsNull(List<Long> ids);

    @Modifying
    @Query("""
        update Vehicle v
        set v.deletedAt = CURRENT_TIMESTAMP
        where v.id in :ids
          and v.deletedAt is null
    """)
    int softDeleteByIdIn(@Param("ids") List<Long> ids);

    // ===== 통계 =====
    @Query("""
        select coalesce(sum(v.personnel), 0)
        from Vehicle v
        where v.deletedAt is null
    """)
    int sumPersonnel();
}
