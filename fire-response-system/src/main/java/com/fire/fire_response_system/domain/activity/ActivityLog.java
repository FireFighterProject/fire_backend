package com.fire.fire_response_system.domain.activity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_vehicle", columnList = "vehicle_id"),
                @Index(name = "idx_activity_started", columnList = "started_at")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "station_id", nullable = false)
    private Long stationId;

    @Column(name = "place")
    private String place; // 출동 장소

    @Column(name = "description")
    private String description; // 활동 내용

    @Column(name = "status", nullable = false)
    private Integer status; // 0=대기, 1=활동, 2=복귀

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "moved_at")
    private LocalDateTime movedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
        if (status == null) status = 1; // 기본 활동 시작
        if (startedAt == null) startedAt = LocalDateTime.now();
    }
    @PreUpdate void preUpdate() { updatedAt = LocalDateTime.now(); }
}