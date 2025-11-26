package com.fire.fire_response_system.domain.gps;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_gps_log",
        indexes = {
                @Index(name = "idx_gps_vehicle", columnList = "vehicle_id"),
                @Index(name = "idx_gps_captured", columnList = "captured_at")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleGpsLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "station_id", nullable = false)
    private Long stationId;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "speed")
    private Double speed;

    @Column(name = "heading")
    private Double heading;

    @Column(name = "captured_at", nullable = false)
    private LocalDateTime capturedAt;

    // ⭐ 평상시 / 재난 로그 구분 (0=평상시, 1=재난)
    @Column(name = "log_mode", nullable = false)
    private Integer logMode;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (capturedAt == null) capturedAt = now;
        if (logMode == null) logMode = 0; // 기본값 = 평상시
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
