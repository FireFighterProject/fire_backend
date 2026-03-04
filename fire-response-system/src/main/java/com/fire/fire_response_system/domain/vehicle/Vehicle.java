package com.fire.fire_response_system.domain.vehicle;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicles_station", columnList = "station_id"),
                @Index(name = "idx_vehicles_status", columnList = "status"),
                @Index(name = "idx_vehicles_deleted", columnList = "deleted_at")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_station_call_sign", columnNames = {"station_id", "call_sign"})
        }
)
@SQLDelete(sql = "UPDATE vehicles SET deleted_at = NOW() WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_id", nullable = false)
    private Long stationId;

    @Column(name = "sido", nullable = false, length = 50)
    private String sido;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "call_sign", nullable = false, length = 100)
    private String callSign;

    private Integer capacity;
    private Integer personnel;

    @Column(name = "avl_number")
    private String avlNumber;

    @Column(name = "ps_lte_number")
    private String psLteNumber;

    /**
     * 차량 상태 (0=대기, 1=활동, 2=철수, 3=집결중)
     */
    private Integer status;

    /**
     * rally_point — 자원 집결지 여부 (O/X)
     */
    @Column(name = "rally_point", length = 1)
    private String rallyPoint;

    /**
     * 출동 횟수 (통계용)
     */
    @Column(name = "dispatch_count", nullable = false)
    private Integer dispatchCount;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Soft Delete용 컬럼
     * null      : 정상 차량
     * not null  : 삭제된 차량
     */
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.dispatchCount == null) this.dispatchCount = 0;
        if (this.status == null) this.status = 0; // 기본 대기
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}