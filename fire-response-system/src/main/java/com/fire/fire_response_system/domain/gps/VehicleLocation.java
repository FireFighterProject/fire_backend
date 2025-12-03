package com.fire.fire_response_system.domain.gps;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_location")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder        // ★ 이거 추가해야 builder() 사용 가능!
public class VehicleLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false, unique = true)
    private Long vehicleId;

    private Double latitude;
    private Double longitude;

    @Column(name = "last_updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;
}
