package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import com.fire.fire_response_system.dto.common.MessageResponse;
import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleBulkService {

    private final VehicleRepository vehicleRepo;

    /** 전체 차량에 GPS 요청 (문자 발송 가정) */
    @Transactional
    public MessageResponse requestGpsAll() {
        long count = vehicleRepo.findAll().stream()
                .filter(v -> v.getAvlNumber() != null && !v.getAvlNumber().isBlank())
                .count();

        return new MessageResponse("GPS 요청 전송 대상 차량 수 = " + count);
    }

    /** 전체 차량을 일괄 철수 처리 */
    @Transactional
    public MessageResponse retireAll() {

        LocalDateTime now = LocalDateTime.now();

        long count = vehicleRepo.findAll().stream()
                .filter(v -> v.getStatus() == null || v.getStatus() != 2)
                .peek(v -> {
                    v.setStatus(2);
                    v.setUpdatedAt(now);
                })
                .count();

        return new MessageResponse("철수 처리된 차량 수 = " + count);
    }
}
