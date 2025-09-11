package com.fire.fire_response_system.domain.gps;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicle_gps_log",
        indexes = @Index(name = "idx_vgps_vehicle_time", columnList = "vehicle_id, sent_at"))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class VehicleGpsLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_id", nullable = false)
    private Long vehicleId;

    @Column(nullable = false) private Double latitude;
    @Column(nullable = false) private Double longitude;

    private Double heading;

    @Column(name = "speed_kph")
    private Double speedKph;

    // 장비가 보낸 시각
    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt;

    // 수신표시(BOOLEAN/TINYINT)
    @Column(name = "gps_received")
    private Boolean gpsReceived;
}
