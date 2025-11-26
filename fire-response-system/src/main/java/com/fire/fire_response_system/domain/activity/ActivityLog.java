package com.fire.fire_response_system.domain.activity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs",
        indexes = {
                @Index(name = "idx_activity_vehicle", columnList = "vehicle_id"),
                @Index(name = "idx_activity_started", columnList = "started_at"),
                @Index(name = "idx_activity_log_mode", columnList = "log_mode")
        })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "station_id", nullable = false)
    private Long stationId;

    @Column(name = "place")
    private String place;            // 출동 장소

    @Column(name = "description")
    private String description;      // 활동 내용

    /**
     * 상태 코드
     * 0 = 대기
     * 1 = 활동 중
     * 2 = 복귀 완료
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "moved_at")
    private LocalDateTime movedAt;

    /**
     * ★ 추가된 필드: 평상시 / 재난 로그 구분
     * 0 = NORMAL
     * 1 = DISASTER
     */
    @Column(name = "log_mode", nullable = false)
    private Integer logMode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.status == null)
            this.status = 1; // 기본 = 활동 시작

        if (this.startedAt == null)
            this.startedAt = LocalDateTime.now();

        if (this.logMode == null)
            this.logMode = 0; // 기본 = NORMAL
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
