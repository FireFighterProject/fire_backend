package com.fire.fire_response_system.domain.gps;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_gps_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder        // ★ 이거 추가해야 builder() 사용 가능!
public class VehicleGpsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    private Double latitude;
    private Double longitude;

    @Column(name = "logged_at", nullable = false)
    private LocalDateTime loggedAt;   // gps 기록 시간
}
