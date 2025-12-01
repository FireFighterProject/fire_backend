package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.dispatch.DispatchAssignment;
import com.fire.fire_response_system.domain.dispatch.DispatchOrder;
import com.fire.fire_response_system.domain.dispatch.DispatchStatus;
import com.fire.fire_response_system.domain.dispatch.DispatchVehicleMap;
import com.fire.fire_response_system.domain.vehicle.Vehicle;
import com.fire.fire_response_system.dto.dispatch.*;
import com.fire.fire_response_system.repository.DispatchAssignmentRepository;
import com.fire.fire_response_system.repository.DispatchOrderRepository;
import com.fire.fire_response_system.repository.DispatchVehicleMapRepository;
import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DispatchOrderService {

    private final DispatchOrderRepository orderRepo;
    private final DispatchAssignmentRepository assignRepo;
    private final DispatchVehicleMapRepository mapRepo;
    private final VehicleRepository vehicleRepo;

    /** 출동명령 생성 (같은 주소·DRAFT 상태면 기존 order 재사용) */
    @Transactional
    public DispatchOrderResponse createOrder(CreateDispatchOrderRequest req) {

        DispatchOrder existing = orderRepo
                .findTopByAddressAndStatusOrderByCreatedAtDesc(
                        req.getAddress(), DispatchStatus.DRAFT
                )
                .orElse(null);

        DispatchOrder order;

        if (existing != null) {
            order = existing;  // 같은 주소 + 진행중이면 재사용
        } else {
            order = DispatchOrder.builder()
                    .title(req.getTitle())
                    .address(req.getAddress())
                    .content(req.getContent())
                    .status(DispatchStatus.DRAFT)
                    .build();

            orderRepo.save(order);
        }

        return DispatchOrderResponse.builder()
                .id(order.getId())
                .title(order.getTitle())
                .address(order.getAddress())
                .content(order.getContent())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }

    /** 차량 편성 – 스마트 배치 방식(B) */
    @Transactional
    public VehicleAssignResponse assignVehicles(Long orderId, VehicleAssignRequest req) {

        DispatchOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("출동명령 없음: " + orderId));

        DispatchAssignment latest = assignRepo
                .findTopByOrderIdOrderByBatchNoDesc(orderId)
                .orElse(null);

        DispatchAssignment batch;

        if (latest == null) {
            batch = createBatch(order, 1);   // 1차 배치 생성
        } else {
            boolean hasVehicles = mapRepo.existsByAssignmentId(latest.getId());

            if (hasVehicles) {
                // 차량이 이미 있으면 다음 차수 배치 생성
                batch = createBatch(order, latest.getBatchNo() + 1);
            } else {
                // 비어있으면 기존 배치 재사용
                batch = latest;
            }
        }

        List<VehicleSummary> resultVehicles = new ArrayList<>();

        for (Long vehicleId : req.getVehicleIds()) {

            Vehicle v = vehicleRepo.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("차량 없음: " + vehicleId));

            if (mapRepo.existsByAssignmentIdAndVehicleId(batch.getId(), vehicleId))
                continue;

            mapRepo.save(
                    DispatchVehicleMap.builder()
                            .assignment(batch)
                            .vehicle(v)
                            .build()
            );

            v.setStatus(1); // 활동
            vehicleRepo.save(v);

            resultVehicles.add(toSummary(v));
        }

        return VehicleAssignResponse.builder()
                .orderId(orderId)
                .batchNo(batch.getBatchNo())
                .vehicles(resultVehicles)
                .build();
    }

    /** 차량 복귀 */
    @Transactional
    public void returnVehicles(Long orderId, VehicleReturnRequest req) {

        orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("출동명령 없음: " + orderId));

        for (Long vehicleId : req.getVehicleIds()) {
            Vehicle v = vehicleRepo.findById(vehicleId)
                    .orElseThrow(() -> new IllegalArgumentException("차량 없음: " + vehicleId));

            v.setStatus(0); // 대기
            vehicleRepo.save(v);
        }
    }

    /** 배치 생성 */
    private DispatchAssignment createBatch(DispatchOrder order, int batchNo) {
        DispatchAssignment a = DispatchAssignment.builder()
                .order(order)
                .batchNo(batchNo)
                .build();
        return assignRepo.save(a);
    }

    private VehicleSummary toSummary(Vehicle v) {
        return VehicleSummary.builder()
                .id(v.getId())
                .stationId(v.getStationId())
                .sido(v.getSido())
                .typeName(v.getTypeName())
                .callSign(v.getCallSign())
                .status(v.getStatus())
                .capacity(v.getCapacity())
                .personnel(v.getPersonnel())
                .avlNumber(v.getAvlNumber())
                .psLteNumber(v.getPsLteNumber())
                .build();
    }

    /** 출동명령 목록 */
    @Transactional(readOnly = true)
    public List<DispatchOrderListItem> listOrders() {

        List<DispatchOrder> orders = orderRepo.findAllByOrderByCreatedAtDesc();

        List<DispatchOrderListItem> result = new ArrayList<>();

        for (DispatchOrder order : orders) {

            // 1) 모든 배치 가져오기
            List<DispatchAssignment> assignments =
                    assignRepo.findByOrderIdOrderByBatchNoAsc(order.getId());

            // 2) 배치에 연결된 모든 차량 flatten
            List<DispatchOrderListItem.AssignedVehicleItem> vehicles = new ArrayList<>();

            for (DispatchAssignment a : assignments) {
                List<DispatchVehicleMap> maps = mapRepo.findByAssignmentId(a.getId());

                for (DispatchVehicleMap m : maps) {
                    vehicles.add(
                            new DispatchOrderListItem.AssignedVehicleItem(
                                    m.getVehicle().getId(),
                                    m.getVehicle().getCallSign()
                            )
                    );
                }
            }

            // 3) 리스트에 추가
            result.add(
                    DispatchOrderListItem.builder()
                            .orderId(order.getId())
                            .title(order.getTitle())
                            .address(order.getAddress())
                            .content(order.getContent())
                            .status(order.getStatus().name())
                            .vehicles(vehicles)
                            .build()
            );
        }

        return result;
    }


    /** 출동명령 상세 */
    @Transactional(readOnly = true)
    public DispatchOrderDetail getOrderDetail(Long orderId) {

        DispatchOrder order = orderRepo.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("출동명령 없음"));

        List<DispatchAssignment> batches =
                assignRepo.findByOrderIdOrderByBatchNoAsc(orderId);

        List<BatchDetail> details = new ArrayList<>();

        for (DispatchAssignment a : batches) {
            List<DispatchVehicleMap> mapping = mapRepo.findByAssignmentId(a.getId());

            List<VehicleSummary> vehicles = mapping.stream()
                    .map(m -> toSummary(m.getVehicle()))
                    .toList();

            details.add(new BatchDetail(a.getBatchNo(), vehicles));
        }

        return new DispatchOrderDetail(
                order.getId(),
                order.getTitle(),
                order.getAddress(),
                order.getContent(),
                order.getStatus().name(),
                details
        );
    }

    /** 특정 배치 상세 */
    @Transactional(readOnly = true)
    public BatchDetail getBatchDetail(Long orderId, Integer batchNo) {

        DispatchAssignment batch = assignRepo
                .findByOrderIdAndBatchNo(orderId, batchNo)
                .orElseThrow(() -> new IllegalArgumentException("배치 없음"));

        List<VehicleSummary> vehicles = mapRepo.findByAssignmentId(batch.getId())
                .stream()
                .map(m -> toSummary(m.getVehicle()))
                .toList();

        return new BatchDetail(batch.getBatchNo(), vehicles);
    }

    @Transactional(readOnly = true)
    public VehicleCurrentDispatchResponse getCurrentDispatchByVehicle(Long vehicleId) {

        DispatchVehicleMap map = mapRepo.findByVehicleId(vehicleId)
                .orElse(null);

        if (map == null) {
            return VehicleCurrentDispatchResponse.notAssigned();
        }

        DispatchAssignment batch = map.getAssignment();
        DispatchOrder order = batch.getOrder();

        return VehicleCurrentDispatchResponse.of(
                order.getId(),
                order.getAddress(),
                order.getContent()
        );
    }

}
