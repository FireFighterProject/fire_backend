package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.vehicle.Vehicle;
import com.fire.fire_response_system.dto.vehicle.*;
import com.fire.fire_response_system.repository.StationRepository;
import com.fire.fire_response_system.repository.VehicleRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehiclesService {

    private final VehicleRepository vehicleRepository;
    private final StationRepository stationRepository;
    private final EntityManager em;

    @Transactional
    public VehicleResponse create(VehicleCreateRequest req) {
        var station = stationRepository.findById(req.getStationId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 stationId"));

        if (vehicleRepository.existsByStationIdAndCallSign(req.getStationId(), req.getCallSign())) {
            throw new IllegalStateException("동일 소방서에 이미 존재하는 callSign");
        }

        Vehicle v = Vehicle.builder()
                .stationId(req.getStationId())
                .sido(req.getSido())
                .callSign(req.getCallSign())
                .typeName(req.getTypeName())
                .capacity(req.getCapacity())
                .personnel(req.getPersonnel())
                .avlNumber(req.getAvlNumber())
                .psLteNumber(req.getPsLteNumber())
                .status(req.getStatus() == null ? 0 : req.getStatus())
                .rallyPoint(req.getRallyPoint() == null ? 0 : req.getRallyPoint())
                .build();

        Vehicle saved = vehicleRepository.save(v);
        return toResponse(saved);
    }

    public List<VehicleListItem> list(Long stationId, Integer status, String typeName, String callSignLike) {
        var cb = em.getCriteriaBuilder();
        var cq = cb.createQuery(Vehicle.class);
        var root = cq.from(Vehicle.class);

        List<Predicate> p = new ArrayList<>();
        if (stationId != null) p.add(cb.equal(root.get("stationId"), stationId));
        if (status != null)    p.add(cb.equal(root.get("status"), status));
        if (typeName != null && !typeName.isBlank()) p.add(cb.equal(root.get("typeName"), typeName));
        if (callSignLike != null && !callSignLike.isBlank()) p.add(cb.like(root.get("callSign"), "%" + callSignLike + "%"));

        cq.where(p.toArray(Predicate[]::new)).orderBy(cb.asc(root.get("stationId")), cb.asc(root.get("callSign")));
        return em.createQuery(cq).getResultList().stream()
                .map(v -> new VehicleListItem(v.getId(), v.getStationId(), v.getSido(),
                        v.getTypeName(), v.getCallSign(), v.getStatus(), v.getRallyPoint()))
                .toList();
    }

    @Transactional
    public VehicleResponse update(Long id, VehicleUpdateRequest req) {
        Vehicle v = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("vehicle 없음"));

        if (req.getCallSign() != null && !req.getCallSign().isBlank()) {
            String newCall = req.getCallSign();
            if (!newCall.equals(v.getCallSign())) {
                boolean dup = vehicleRepository.existsByStationIdAndCallSignAndIdNot(v.getStationId(), newCall, v.getId());
                if (dup) throw new IllegalStateException("동일 소방서에 이미 존재하는 callSign");
                v.setCallSign(newCall);
            }
        }
        if (req.getTypeName() != null)  v.setTypeName(req.getTypeName());
        if (req.getCapacity() != null)  v.setCapacity(req.getCapacity());
        if (req.getPersonnel() != null) v.setPersonnel(req.getPersonnel());
        if (req.getAvlNumber() != null) v.setAvlNumber(req.getAvlNumber());
        if (req.getPsLteNumber() != null) v.setPsLteNumber(req.getPsLteNumber());

        return toResponse(v);
    }

    @Transactional
    public VehicleResponse updateStatus(Long id, Integer status) {
        if (status == null || status < 0 || status > 2) {
            throw new IllegalArgumentException("status는 0/1/2만 허용");
        }
        Vehicle v = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("vehicle 없음"));
        v.setStatus(status);
        return toResponse(v);
    }

    @Transactional
    public VehicleResponse updateAssembly(Long id, Integer rallyPoint) {
        Vehicle v = vehicleRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("vehicle 없음"));
        if (rallyPoint == null) {
            v.setRallyPoint(v.getRallyPoint() != null && v.getRallyPoint() == 1 ? 0 : 1);
        } else {
            if (rallyPoint != 0 && rallyPoint != 1) throw new IllegalArgumentException("rallyPoint는 0/1만 허용");
            v.setRallyPoint(rallyPoint);
        }
        return toResponse(v);
    }

    private static VehicleResponse toResponse(Vehicle v) {
        return new VehicleResponse(
                v.getId(), v.getStationId(), v.getSido(), // 시도 포함
                v.getTypeName(), v.getCallSign(),
                v.getCapacity(), v.getPersonnel(), v.getAvlNumber(), v.getPsLteNumber(),
                v.getStatus(), v.getRallyPoint(), v.getCreatedAt(), v.getUpdatedAt()
        );
    }
}
