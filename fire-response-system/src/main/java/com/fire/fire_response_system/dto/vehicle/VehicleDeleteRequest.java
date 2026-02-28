package com.fire.fire_response_system.dto.vehicle;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class VehicleDeleteRequest {

    @NotEmpty
    private List<Long> vehicleIds;
}
