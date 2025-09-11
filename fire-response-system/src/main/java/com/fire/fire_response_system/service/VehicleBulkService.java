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

    /**
     * 전체 차량 대상으로 GPS 수신 요청 (문자 발송 가정)
     * 실제 발송은 외부 모듈과 연동 필요. 여기서는 "요청됨" 상태만 표시.
     */
    @Transactional
    public MessageResponse requestGpsAll() {
        List<Vehicle> vehicles = vehicleRepo.findAll();

        // 실제 구현에서는 AVL 번호를 모아서 외부 문자 발송 API 호출
        int count = 0;
        for (Vehicle v : vehicles) {
            if (v.getAvlNumber() != null && !v.getAvlNumber().isBlank()) {
                // 아직 GPS 수신되지 않은 경우만 처리 (여기선 단순히 로그만 남김)
                count++;
            }
        }
        return new MessageResponse("GPS 요청 전송 대상 차량 수=" + count);
    }

    /**
     * 전체 차량을 일괄 철수 처리
     */
    @Transactional
    public MessageResponse retireAll() {
        List<Vehicle> vehicles = vehicleRepo.findAll();
        int count = 0;
        for (Vehicle v : vehicles) {
            if (v.getStatus() != null && v.getStatus() != 2) { // 2=철수
                v.setStatus(2);
                v.setUpdatedAt(LocalDateTime.now());
                count++;
            }
        }
        return new MessageResponse("철수 처리된 차량 수=" + count);
    }
}