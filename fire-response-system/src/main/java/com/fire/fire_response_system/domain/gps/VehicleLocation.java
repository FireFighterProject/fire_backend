package com.fire.fire_response_system.domain.gps;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_location")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleLocation {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    private Double latitude;
    private Double longitude;
    private Double heading;

    @Column(name = "speed_kph")
    private Double speedKph;

    // 최신 갱신시각 (서비스에서 직접 세팅)
    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;
}
