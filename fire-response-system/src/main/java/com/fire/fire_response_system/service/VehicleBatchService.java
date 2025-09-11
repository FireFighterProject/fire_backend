package com.fire.fire_response_system.service;

import com.fire.fire_response_system.dto.vehicle.VehicleBatchRequest;
import com.fire.fire_response_system.dto.vehicle.VehicleBatchResponse;
import com.fire.fire_response_system.domain.vehicle.Vehicle;
import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleBatchService {

    private final VehicleRepository vehicleRepo;

    @Transactional
    public VehicleBatchResponse register(List<VehicleBatchRequest> requests) {
        VehicleBatchResponse res = VehicleBatchResponse.empty();
        int inserted = 0, duplicates = 0;

        for (int i = 0; i < requests.size(); i++) {
            VehicleBatchRequest req = requests.get(i);

            // 중복 체크
            if (vehicleRepo.existsByStationIdAndCallSign(req.getStationId(), req.getCallSign())) {
                duplicates++;
                res.getMessages().add("중복 스킵: row=" + (i + 1) + ", callSign=" + req.getCallSign());
                continue;
            }

            // rallyPoint 규칙 적용
            int rally = "경북".equals(req.getProvince()) ? 0 : 1;

            Vehicle v = Vehicle.builder()
                    .stationId(req.getStationId())
                    .typeName(req.getTypeName())
                    .callSign(req.getCallSign())
                    .capacity(req.getCapacity())
                    .personnel(req.getPersonnel())
                    .avlNumber(req.getAvlNumber())
                    .psLteNumber(req.getPsLteNumber())
                    .status(0) // 기본: 대기
                    .rallyPoint(rally)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            vehicleRepo.save(v);
            inserted++;
        }

        res.setTotal(requests.size());
        res.setInserted(inserted);
        res.setDuplicates(duplicates);
        return res;
    }
}