package com.fire.fire_response_system.domain.dispatch;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "vehicle_dispatch_log")
public class VehicleDispatchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(name = "managing_station_id", nullable = false)
    private Long managingStationId;

    @Column(name = "dispatch_order_id", nullable = false)
    private Long dispatchOrderId;

    @Column(name = "dispatch_batch_id")
    private Long dispatchBatchId;

    @Column(name = "given_at", nullable = false)
    private LocalDateTime givenAt;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    @Column(name = "order_count_at_given", nullable = false)
    private Integer orderCountAtGiven;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
