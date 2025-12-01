package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import com.fire.fire_response_system.domain.station.Station;
import com.fire.fire_response_system.dto.vehicle.*;
import com.fire.fire_response_system.repository.StationRepository;
import com.fire.fire_response_system.repository.VehicleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiclesService {

    private final VehicleRepository vehicleRepository;
    private final StationRepository stationRepository;
    private final EntityManager em;

    // ---------------------------
    // 1) 차량 단건 등록
    // ---------------------------
    @Transactional
    public VehicleResponse create(VehicleCreateRequest req) {

        Station station = stationRepository
                .findBySidoAndName(req.getSido(), req.getStationName())
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 소방서: "
                                + req.getSido() + " " + req.getStationName()));

        Long stationId = station.getId();

        if (vehicleRepository.existsByStationIdAndCallSign(stationId, req.getCallSign())) {
            throw new IllegalStateException("동일 소방서에 이미 존재하는 callSign");
        }

        String rally = "경북".equals(req.getSido()) ? "X" : "O";

        Vehicle v = Vehicle.builder()
                .stationId(stationId)
                .sido(req.getSido())
                .callSign(req.getCallSign())
                .typeName(req.getTypeName())
                .capacity(req.getCapacity())
                .personnel(req.getPersonnel())
                .avlNumber(req.getAvlNumber())
                .psLteNumber(req.getPsLteNumber())
                .status(0)
                .rallyPoint(rally)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        var saved = vehicleRepository.save(v);
        return toResponse(saved);
    }

    // ---------------------------
    // 2) 차량 다건 등록
    // ---------------------------
    @Transactional
    public VehicleBatchResponse registerBatch(List<VehicleBatchRequest> requests) {

        VehicleBatchResponse res = VehicleBatchResponse.empty();
        int inserted = 0, duplicates = 0;

        for (int i = 0; i < requests.size(); i++) {

            VehicleBatchRequest req = requests.get(i);

            Station station = stationRepository
                    .findBySidoAndName(req.getSido(), req.getStationName())
                    .orElse(null);

            if (station == null) {
                res.getMessages().add("소방서 없음 → 스킵: row=" + (i + 1));
                continue;
            }

            Long stationId = station.getId();

            if (vehicleRepository.existsByStationIdAndCallSign(stationId, req.getCallSign())) {
                duplicates++;
                res.getMessages().add("중복 스킵: row=" + (i + 1));
                continue;
            }

            String rally = "경북".equals(req.getSido()) ? "X" : "O";

            Vehicle v = Vehicle.builder()
                    .stationId(stationId)
                    .sido(req.getSido())
                    .typeName(req.getTypeName())
                    .callSign(req.getCallSign())
                    .capacity(req.getCapacity())
                    .personnel(req.getPersonnel())
                    .avlNumber(req.getAvlNumber())
                    .psLteNumber(req.getPsLteNumber())
                    .status(0)
                    .rallyPoint(rally)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            vehicleRepository.save(v);
            inserted++;
        }

        res.setTotal(requests.size());
        res.setInserted(inserted);
        res.setDuplicates(duplicates);
        return res;
    }

    // ---------------------------
    // 3) 차량 목록 조회
    // ---------------------------
    public List<VehicleListItem> list(Long stationId, Integer status,
                                      String typeName, String callSignLike) {

        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Vehicle.class);
        var root = cq.from(Vehicle.class);

        List<Predicate> p = new ArrayList<>();

        if (stationId != null) p.add(cb.equal(root.get("stationId"), stationId));
        if (status != null) p.add(cb.equal(root.get("status"), status));
        if (typeName != null && !typeName.isBlank())
            p.add(cb.equal(root.get("typeName"), typeName));
        if (callSignLike != null && !callSignLike.isBlank())
            p.add(cb.like(root.get("callSign"), "%" + callSignLike + "%"));

        cq.where(p.toArray(Predicate[]::new))
                .orderBy(cb.asc(root.get("stationId")), cb.asc(root.get("callSign")));

        return em.createQuery(cq).getResultList()
                .stream()
                .map(v -> new VehicleListItem(
                        v.getId(),
                        v.getStationId(),
                        v.getSido(),
                        v.getTypeName(),
                        v.getCallSign(),
                        v.getStatus(),
                        v.getRallyPoint(),        // rallyPoint as String
                        v.getCapacity(),
                        v.getPersonnel(),
                        v.getAvlNumber(),
                        v.getPsLteNumber()
                ))
                .toList();
    }

    // ---------------------------
    // 4) 차량 정보 수정
    // ---------------------------
    @Transactional
    public VehicleResponse update(Long id, VehicleUpdateRequest req) {

        var v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("vehicle 없음"));

        if (req.getCallSign() != null && !req.getCallSign().isBlank()) {
            String newCall = req.getCallSign();

            if (!newCall.equals(v.getCallSign())) {
                boolean dup = vehicleRepository
                        .existsByStationIdAndCallSignAndIdNot(
                                v.getStationId(), newCall, v.getId());

                if (dup)
                    throw new IllegalStateException("동일 소방서에 이미 callSign 존재");

                v.setCallSign(newCall);
            }
        }

        if (req.getTypeName() != null) v.setTypeName(req.getTypeName());
        if (req.getCapacity() != null) v.setCapacity(req.getCapacity());
        if (req.getPersonnel() != null) v.setPersonnel(req.getPersonnel());
        if (req.getAvlNumber() != null) v.setAvlNumber(req.getAvlNumber());
        if (req.getPsLteNumber() != null) v.setPsLteNumber(req.getPsLteNumber());

        return toResponse(v);
    }

    // ---------------------------
    // 5) 차량 상태 변경
    // ---------------------------
    @Transactional
    public VehicleResponse updateStatus(Long id, Integer status) {

        if (status == null || status < 0 || status > 2)
            throw new IllegalArgumentException("status는 0/1/2만 허용");

        var v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("vehicle 없음"));

        v.setStatus(status);
        return toResponse(v);
    }

    // ---------------------------
    // 6) 집결지 변경 (토글)
    // ---------------------------
    @Transactional
    public VehicleResponse updateAssembly(Long id, Integer rallyPoint) {

        var v = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("vehicle 없음"));

        if (rallyPoint == null) {
            // toggle
            String newRally = "O".equals(v.getRallyPoint()) ? "X" : "O";
            v.setRallyPoint(newRally);

        } else {
            if (rallyPoint != 0 && rallyPoint != 1)
                throw new IllegalArgumentException("rallyPoint는 0/1만 허용");

            v.setRallyPoint(rallyPoint == 1 ? "O" : "X");
        }

        return toResponse(v);
    }

    private static VehicleResponse toResponse(Vehicle v) {
        return new VehicleResponse(
                v.getId(), v.getStationId(), v.getSido(),
                v.getTypeName(), v.getCallSign(),
                v.getCapacity(), v.getPersonnel(),
                v.getAvlNumber(), v.getPsLteNumber(),
                v.getStatus(), v.getRallyPoint(),
                v.getCreatedAt(), v.getUpdatedAt()
        );
    }
}
