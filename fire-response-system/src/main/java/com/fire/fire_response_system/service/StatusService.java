package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import com.fire.fire_response_system.dto.status.StatusSummaryResponse;
import com.fire.fire_response_system.dto.status.StatusSummaryRow;
import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final VehicleRepository vehicleRepo;

    @Transactional(readOnly = true)
    public StatusSummaryResponse getSummary(String mode) {
        List<Vehicle> vehicles = vehicleRepo.findAll();

        Map<String, List<Vehicle>> byProvince = new HashMap<>();

        if ("NORMAL".equalsIgnoreCase(mode)) {
            // 평상시 → 경북 전체만
            List<Vehicle> gb = vehicles.stream()
                    .filter(v -> "경북".equals(v.getTypeName()) || "경북".equals(v.getStationId())) // TODO: province 필드가 있으면 교체
                    .collect(Collectors.toList());
            byProvince.put("경북", gb);
        } else {
            // 재난시
            // 1) 경북 중 rallyPoint=1만
            List<Vehicle> gb = vehicles.stream()
                    .filter(v -> "경북".equals(v.getTypeName()) || "경북".equals(v.getStationId())) // TODO: province 필드 교체
                    .filter(v -> v.getRallyPoint() != null && v.getRallyPoint() == 1)
                    .collect(Collectors.toList());
            byProvince.put("경북", gb);

            // 2) 그 외 지역
            vehicles.stream()
                    .filter(v -> v.getStationId() != null) // stationId 기반 province 매핑 필요
                    .collect(Collectors.groupingBy(Vehicle::getTypeName)) // TODO: province로 교체
                    .forEach(byProvince::put);
        }

        List<StatusSummaryRow> rows = new ArrayList<>();
        for (Map.Entry<String, List<Vehicle>> e : byProvince.entrySet()) {
            String province = e.getKey();
            List<Vehicle> list = e.getValue();

            // 전체
            rows.add(buildRow(province + " 전체", list));

            // 대기
            rows.add(buildRow(province + " 대기", list.stream()
                    .filter(v -> v.getStatus() != null && v.getStatus() == 0).toList()));

            // 활동
            rows.add(buildRow(province + " 활동", list.stream()
                    .filter(v -> v.getStatus() != null && v.getStatus() == 1).toList()));
        }

        return StatusSummaryResponse.builder()
                .mode(mode.toUpperCase())
                .rows(rows)
                .build();
    }

    private StatusSummaryRow buildRow(String region, List<Vehicle> vehicles) {
        int totalVehicles = vehicles.size();
        int totalPersonnel = vehicles.stream()
                .map(v -> Optional.ofNullable(v.getPersonnel()).orElse(0))
                .reduce(0, Integer::sum);

        Map<String, Integer> typeCounts = vehicles.stream()
                .collect(Collectors.groupingBy(
                        v -> Optional.ofNullable(v.getTypeName()).orElse("기타"),
                        Collectors.summingInt(v -> 1)
                ));

        return StatusSummaryRow.builder()
                .region(region)
                .totalVehicles(totalVehicles)
                .totalPersonnel(totalPersonnel)
                .typeCounts(typeCounts)
                .build();
    }
}