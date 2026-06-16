package com.fire.fire_response_system.dto.vehicle;

public record VehicleListItem(
        Long id,
        Long stationId,
        String sido,
        String typeName,
        String callSign,
        Integer status,
        String rallyPoint,
        Integer capacity,
        Integer personnel,
        String avlNumber,
        String psLteNumber,
        String phoneNumber
) {}
