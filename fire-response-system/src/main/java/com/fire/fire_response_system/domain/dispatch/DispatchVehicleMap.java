package com.fire.fire_response_system.domain.dispatch;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_vehicle_map",
        indexes = {
                @Index(name = "idx_dvm_order", columnList = "dispatch_order_id"),
                @Index(name = "idx_dvm_order_vehicle", columnList = "dispatch_order_id,vehicle_id", unique = true)
        })
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class DispatchVehicleMap {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dispatch_order_id", nullable = false)
    private Long dispatchOrderId;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Convert(converter = DispatchVehicleStatusConverter.class)
    @Column(nullable = false)
    private DispatchVehicleStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
