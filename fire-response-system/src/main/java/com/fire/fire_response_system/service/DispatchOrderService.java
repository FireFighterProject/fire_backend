package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.dispatch.DispatchOrder;
import com.fire.fire_response_system.domain.dispatch.DispatchStatus;
import com.fire.fire_response_system.domain.dispatch.DispatchVehicleMap;
import com.fire.fire_response_system.domain.dispatch.DispatchVehicleStatus;
import com.fire.fire_response_system.dto.dispatch.CreateDispatchOrderRequest;
import com.fire.fire_response_system.dto.dispatch.UpdateDispatchOrderRequest;
import com.fire.fire_response_system.repository.DispatchOrderRepository;
import com.fire.fire_response_system.repository.DispatchVehicleMapRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchOrderService {

    private final DispatchOrderRepository orderRepo;
    private final DispatchVehicleMapRepository mapRepo;

    /** 출동 명령 생성 */
    @Transactional
    public Long create(CreateDispatchOrderRequest req) {
        if (req.getTitle() == null || req.getTitle().isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        LocalDateTime now = LocalDateTime.now();
        DispatchOrder order = DispatchOrder.builder()
                .stationId(req.getStationId())
                .title(req.getTitle().trim())
                .description(req.getDescription())
                .status(DispatchStatus.DRAFT)
                .createdAt(now)
                .updatedAt(now)
                .build();
        return orderRepo.save(order).getId();
    }

    /** 출동 명령 목록 (상태 필터 선택) */
    @Transactional(readOnly = true)
    public List<DispatchOrder> list(Integer statusCode) {
        if (statusCode == null) return orderRepo.findAll();
        DispatchStatus st = DispatchStatus.from(statusCode);
        return orderRepo.findByStatus(st);
    }

    /** 제목/내용 부분 수정 */
    @Transactional
    public void update(Long id, UpdateDispatchOrderRequest req) {
        DispatchOrder order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dispatch order not found: " + id));
        if (req.getTitle() != null) order.setTitle(req.getTitle().trim());
        if (req.getDescription() != null) order.setDescription(req.getDescription());
        order.setUpdatedAt(LocalDateTime.now());
        // “수정 시 자동 재발송” 로직이 필요하면 여기서 처리
    }

    /** 삭제 (작성중/종료만 허용) */
    @Transactional
    public void delete(Long id) {
        DispatchOrder order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dispatch order not found: " + id));
        if (!(order.getStatus() == DispatchStatus.DRAFT || order.getStatus() == DispatchStatus.ENDED)) {
            throw new IllegalStateException("SENT 상태에서는 삭제 불가");
        }
        // 관련 매핑 삭제
        mapRepo.findByDispatchOrderId(id).forEach(m -> mapRepo.deleteById(m.getId()));
        orderRepo.delete(order);
    }

    /** 발송 → SENT */
    @Transactional
    public void send(Long id) {
        DispatchOrder order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dispatch order not found: " + id));
        if (order.getStatus() != DispatchStatus.DRAFT) {
            throw new IllegalStateException("DRAFT 상태에서만 발송 가능");
        }
        order.setStatus(DispatchStatus.SENT);
        order.setUpdatedAt(LocalDateTime.now());

        // 매핑된 차량 상태도 SENT 로
        mapRepo.findByDispatchOrderId(id).forEach(m -> {
            m.setStatus(DispatchVehicleStatus.SENT);
            m.setUpdatedAt(LocalDateTime.now());
            mapRepo.save(m);
        });
        // 실제 문자/푸시 발송 연동 위치
    }

    /** 종료 → ENDED */
    @Transactional
    public void end(Long id) {
        DispatchOrder order = orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("dispatch order not found: " + id));
        if (order.getStatus() == DispatchStatus.ENDED) return;
        order.setStatus(DispatchStatus.ENDED);
        order.setUpdatedAt(LocalDateTime.now());
    }

    /** 차량 편성 추가 (중복 시 무시) */
    @Transactional
    public void assignVehicle(Long orderId, Long vehicleId) {
        if (vehicleId == null) throw new IllegalArgumentException("vehicleId is required");
        if (mapRepo.existsByDispatchOrderIdAndVehicleId(orderId, vehicleId)) return;

        LocalDateTime now = LocalDateTime.now();
        DispatchVehicleMap map = DispatchVehicleMap.builder()
                .dispatchOrderId(orderId)
                .vehicleId(vehicleId)
                .status(DispatchVehicleStatus.PENDING)
                .createdAt(now)
                .updatedAt(now)
                .build();
        mapRepo.save(map);
    }

    /** 차량 편성 제거 */
    @Transactional
    public void unassignVehicle(Long orderId, Long vehicleId) {
        mapRepo.findByDispatchOrderIdAndVehicleId(orderId, vehicleId)
                .ifPresent(map -> mapRepo.deleteById(map.getId()));
    }

    /** 편성 차량 상태 변경 */
    @Transactional
    public void updateVehicleStatus(Long orderId, Long vehicleId, int statusCode) {
        DispatchVehicleMap map = mapRepo.findByDispatchOrderIdAndVehicleId(orderId, vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("vehicle not assigned to order"));
        map.setStatus(DispatchVehicleStatus.from(statusCode));
        map.setUpdatedAt(LocalDateTime.now());
        mapRepo.save(map);
    }
}
