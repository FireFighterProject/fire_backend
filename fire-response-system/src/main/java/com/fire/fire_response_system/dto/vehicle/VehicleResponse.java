package com.fire.fire_response_system.dto.vehicle;

import java.time.LocalDateTime;

public record VehicleResponse(
        Long id,
        Long stationId,        //  Long
        String sido,           //  String
        String typeName,
        String callSign,
        Integer capacity,
        Integer personnel,
        String avlNumber,
        String psLteNumber,
        Integer status,
        Integer rallyPoint,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
