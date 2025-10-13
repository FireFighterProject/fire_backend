package com.fire.fire_response_system.dto.station;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class StationItem {
    private Long id;
    private String name;
    private String sido;     // 시·도
    private String address;
}
