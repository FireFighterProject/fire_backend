package com.fire.fire_response_system.domain.gps;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_location")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    private Double latitude;
    private Double longitude;

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
