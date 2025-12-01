package com.fire.fire_response_system.domain.vehicle;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicles_station", columnList = "station_id"),
                @Index(name = "idx_vehicles_status", columnList = "status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_station_call_sign", columnNames = {"station_id","call_sign"})
        })
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

    private Integer status;

    /**
     * rally_point — 자원 집결지 여부 (O/X)
     * String으로 수정해 Hibernate 타입 오류 방지
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

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.dispatchCount == null) {
            this.dispatchCount = 0;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
