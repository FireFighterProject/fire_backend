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

    private DispatchOrder getOrder(Long id) {
        return orderRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dispatch order not found: " + id));
    }

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

    @Transactional(readOnly = true)
    public List<DispatchOrder> list(Integer statusCode) {
        if (statusCode == null) return orderRepo.findAll();
        return orderRepo.findByStatus(DispatchStatus.from(statusCode));
    }

    @Transactional
    public void update(Long id, UpdateDispatchOrderRequest req) {
        DispatchOrder order = getOrder(id);

        if (req.getTitle() != null) order.setTitle(req.getTitle().trim());
        if (req.getDescription() != null) order.setDescription(req.getDescription());

        order.setUpdatedAt(LocalDateTime.now());
    }

    @Transactional
    public void delete(Long id) {
        DispatchOrder order = getOrder(id);

        if (!(order.getStatus() == DispatchStatus.DRAFT || order.getStatus() == DispatchStatus.ENDED)) {
            throw new IllegalStateException("SENT 상태에서는 삭제 불가");
        }

        mapRepo.deleteAll(mapRepo.findByDispatchOrderId(id));
        orderRepo.delete(order);
    }

    @Transactional
    public void send(Long id) {
        DispatchOrder order = getOrder(id);

        if (order.getStatus() != DispatchStatus.DRAFT) {
            throw new IllegalStateException("DRAFT 상태에서만 발송 가능");
        }

        order.setStatus(DispatchStatus.SENT);
        order.setUpdatedAt(LocalDateTime.now());

        List<DispatchVehicleMap> maps = mapRepo.findByDispatchOrderId(id);
        LocalDateTime now = LocalDateTime.now();

        for (DispatchVehicleMap m : maps) {
            m.setStatus(DispatchVehicleStatus.SENT);
            m.setUpdatedAt(now);
        }
    }

    @Transactional
    public void end(Long id) {
        DispatchOrder order = getOrder(id);
        if (order.getStatus() != DispatchStatus.ENDED) {
            order.setStatus(DispatchStatus.ENDED);
            order.setUpdatedAt(LocalDateTime.now());
        }
    }

    @Transactional
    public void assignVehicle(Long orderId, Long vehicleId) {
        if (vehicleId == null) throw new IllegalArgumentException("vehicleId required");

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

    @Transactional
    public void unassignVehicle(Long orderId, Long vehicleId) {
        mapRepo.findByDispatchOrderIdAndVehicleId(orderId, vehicleId)
                .ifPresent(map -> mapRepo.deleteById(map.getId()));
    }

    @Transactional
    public void updateVehicleStatus(Long orderId, Long vehicleId, int statusCode) {
        DispatchVehicleMap map = mapRepo.findByDispatchOrderIdAndVehicleId(orderId, vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("vehicle not assigned"));

        map.setStatus(DispatchVehicleStatus.from(statusCode));
        map.setUpdatedAt(LocalDateTime.now());
    }
}
