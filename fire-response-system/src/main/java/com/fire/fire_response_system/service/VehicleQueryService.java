package com.fire.fire_response_system.service;

import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleQueryService {
    private final VehicleRepository vehicleRepository;

    public List<String> getVehicleTypes() {
        return vehicleRepository.findDistinctTypeNames();
    }
}
