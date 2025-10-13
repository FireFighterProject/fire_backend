package com.fire.fire_response_system.dto.vehicle;

public record VehicleListItem(
        Long id,
        Long stationId,   // Long
        String sido,      // String
        String typeName,
        String callSign,
        Integer status,
        Integer rallyPoint
) {}
