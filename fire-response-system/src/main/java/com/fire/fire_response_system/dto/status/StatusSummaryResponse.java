package com.fire.fire_response_system.dto.status;

import lombok.*;

import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class StatusSummaryResponse {
    private String mode; // "NORMAL" or "DISASTER"
    private List<StatusSummaryRow> rows;
}