package com.fire.fire_response_system.domain.dispatch;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispatch_orders")
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class DispatchOrder {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "station_id", nullable = false)
    private Long stationId;

    private String title;

    private String description;

    @Convert(converter = DispatchStatusConverter.class)
    @Column(name = "order_status", nullable = false)
    private DispatchStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
