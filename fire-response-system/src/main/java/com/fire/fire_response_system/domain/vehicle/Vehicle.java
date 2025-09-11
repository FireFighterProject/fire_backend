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
                // 같은 소방서 안에서 호출부호(call_sign) 중복 방지
                @UniqueConstraint(name = "uk_station_call_sign", columnNames = {"station_id", "call_sign"})
        })
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Vehicle {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_id", nullable = false)
    private Long stationId;

    @Column(name = "type_name")
    private String typeName;

    @Column(name = "call_sign", nullable = false, length = 100)
    private String callSign;

    private Integer capacity;     // 적재량(선택)
    private Integer personnel;    // 탑승 정원(선택)

    @Column(name = "avl_number")
    private String avlNumber;     // 선택

    @Column(name = "ps_lte_number")
    private String psLteNumber;   // 선택

    /**
     * 0=대기, 1=활동, 2=철수
     */
    private Integer status;

    /**
     * 집결지 여부 (0/1)
     */
    @Column(name = "rally_point")
    private Integer rallyPoint;

    @Column(name = "created_at")
    private LocalDateTime createdAt;   // DB 기본값 사용

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;   // DB ON UPDATE 사용
}
