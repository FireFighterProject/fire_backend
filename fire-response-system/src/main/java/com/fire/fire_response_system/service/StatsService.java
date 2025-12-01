package com.fire.fire_response_system.service;

import com.fire.fire_response_system.dto.stats.StatsResponse;
import com.fire.fire_response_system.repository.StationRepository;
import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StationRepository stationRepository;
    private final VehicleRepository vehicleRepository;

    public StatsResponse getStats() {

        // 차량 personnel 총합 = 실제 소방 인원수 대용
        int firefighterCount = vehicleRepository.sumPersonnel();

        int activeStations = (int) stationRepository.count();

        int totalVehicles = (int) vehicleRepository.count();

        return new StatsResponse(
                firefighterCount,
                activeStations,
                totalVehicles
        );
    }
}
