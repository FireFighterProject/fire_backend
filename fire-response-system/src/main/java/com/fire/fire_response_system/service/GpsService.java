package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.gps.VehicleGpsLog;
import com.fire.fire_response_system.domain.gps.VehicleLocation;
import com.fire.fire_response_system.dto.gps.*;
import com.fire.fire_response_system.repository.VehicleGpsLogRepository;
import com.fire.fire_response_system.repository.VehicleLocationRepository;
import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GpsService {

    private final VehicleRepository vehicleRepository;
    private final VehicleGpsLogRepository gpsLogRepo;
    private final VehicleLocationRepository locRepo;

    @Transactional
    public void receive(GpsSendRequest req) {
        if (!vehicleRepository.existsById(req.getVehicleId())) {
            throw new IllegalArgumentException("vehicleId 없음: " + req.getVehicleId());
        }
        LocalDateTime at = (req.getSentAt() != null) ? req.getSentAt() : LocalDateTime.now();

        // 로그 적재
        VehicleGpsLog log = VehicleGpsLog.builder()
                .vehicleId(req.getVehicleId())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .heading(req.getHeading())
                .speedKph(req.getSpeedKph())
                .sentAt(at)
                .gpsReceived(true)
                .build();
        gpsLogRepo.save(log);

        // 최신 위치 업서트
        VehicleLocation cur = locRepo.findByVehicleId(req.getVehicleId())
                .orElseGet(() -> VehicleLocation.builder().vehicleId(req.getVehicleId()).build());
        cur.setLatitude(req.getLatitude());
        cur.setLongitude(req.getLongitude());
        cur.setHeading(req.getHeading());
        cur.setSpeedKph(req.getSpeedKph());
        cur.setLastUpdatedAt(LocalDateTime.now()); // 또는 at (장비시각 사용하고 싶으면 at)
        locRepo.save(cur);
    }

    @Transactional
    public GpsSendBatchResponse receiveAll(GpsSendBatchRequest batch) {
        int ok = 0, fail = 0;
        for (GpsSendRequest r : batch.getItems()) {
            try { receive(r); ok++; } catch (Exception e) { fail++; }
        }
        return new GpsSendBatchResponse(ok, fail);
    }

    public Page<GpsLogItem> logs(Long vehicleId, LocalDateTime from, LocalDateTime to, int page, int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<VehicleGpsLog> pg = (from != null && to != null)
                ? gpsLogRepo.findByVehicleIdAndSentAtBetweenOrderBySentAtDesc(vehicleId, from, to, pageable)
                : gpsLogRepo.findByVehicleIdOrderBySentAtDesc(vehicleId, pageable);

        return pg.map(l -> new GpsLogItem(
                l.getId(), l.getVehicleId(), l.getLatitude(), l.getLongitude(),
                l.getHeading(), l.getSpeedKph(), l.getSentAt()
        ));
    }

    public List<VehicleStatusItem> status(Long stationId, Integer withinMinutes) {
        int minutes = (withinMinutes == null || withinMinutes <= 0) ? 3 : withinMinutes;
        LocalDateTime th = LocalDateTime.now().minusMinutes(minutes);

        var vehicles = (stationId == null)
                ? vehicleRepository.findAll()
                : vehicleRepository.findAll().stream().filter(v -> stationId.equals(v.getStationId())).toList();

        var currents = locRepo.findByLastUpdatedAtGreaterThanEqual(th);
        Map<Long, VehicleLocation> map = new HashMap<>();
        for (var c : currents) map.put(c.getVehicleId(), c);

        List<VehicleStatusItem> result = new ArrayList<>();
        for (var v : vehicles) {
            var cur = map.get(v.getId());
            result.add(new VehicleStatusItem(v.getId(), cur != null, cur != null ? cur.getLastUpdatedAt() : null));
        }
        return result;
    }

    public List<VehicleLocationItem> lastLocations(Long stationId, List<Long> vehicleIds) {
        Set<Long> target = new HashSet<>();
        if (vehicleIds != null && !vehicleIds.isEmpty()) {
            target.addAll(vehicleIds);
        } else if (stationId != null) {
            vehicleRepository.findAll().stream()
                    .filter(v -> stationId.equals(v.getStationId()))
                    .forEach(v -> target.add(v.getId()));
        } else {
            vehicleRepository.findAll().forEach(v -> target.add(v.getId()));
        }
        return locRepo.findByVehicleIdIn(target).stream()
                .map(c -> new VehicleLocationItem(
                        c.getVehicleId(), c.getLatitude(), c.getLongitude(), c.getHeading(), c.getSpeedKph()))
                .toList();
    }

    /** ✅ 지도 드래그 영역 차량 및 통계 조회 */
    @Transactional(readOnly = true)
    public MapStatsResponse mapStats(MapStatsRequest req) {
        if (req == null || req.getMinLat() == null || req.getMaxLat() == null
                || req.getMinLng() == null || req.getMaxLng() == null) {
            throw new IllegalArgumentException("bounds(minLat/maxLat/minLng/maxLng) 누락");
        }

        // bounds 정규화 (min <= max 보장)
        double minLat = Math.min(req.getMinLat(), req.getMaxLat());
        double maxLat = Math.max(req.getMinLat(), req.getMaxLat());
        double minLng = Math.min(req.getMinLng(), req.getMaxLng());
        double maxLng = Math.max(req.getMinLng(), req.getMaxLng());

        // 현재 위치 + 차량 정보 메모리 조인 (상태=1: 활동만)
        var vehicleMap = vehicleRepository.findAll().stream()
                .filter(v -> v.getStatus() != null && v.getStatus() == 1)
                .collect(Collectors.toMap(v -> v.getId(), v -> v));

        var inBounds = locRepo.findAll().stream()
                .filter(l -> l.getLatitude() != null && l.getLongitude() != null)
                .filter(l -> l.getLatitude() >= minLat && l.getLatitude() <= maxLat)
                .filter(l -> l.getLongitude() >= minLng && l.getLongitude() <= maxLng)
                .map(l -> {
                    var v = vehicleMap.get(l.getVehicleId());
                    if (v == null) return null; // 활동 차량만
                    return MapStatsResponse.MapVehicleItem.builder()
                            .vehicleId(v.getId())
                            .callSign(v.getCallSign())
                            .typeName(v.getTypeName())
                            .lat(l.getLatitude())
                            .lng(l.getLongitude())
                            .personnel(v.getPersonnel())
                            .build();
                })
                .filter(Objects::nonNull)
                .toList();

        int totalVehicles = inBounds.size();
        int totalPersonnel = inBounds.stream()
                .map(i -> i.getPersonnel() == null ? 0 : i.getPersonnel())
                .reduce(0, Integer::sum);

        Map<String, Integer> typeCounts = inBounds.stream()
                .collect(Collectors.groupingBy(
                        v -> v.getTypeName() == null ? "기타" : v.getTypeName(),
                        Collectors.summingInt(x -> 1)
                ));

        return MapStatsResponse.builder()
                .totalVehicles(totalVehicles)
                .totalPersonnel(totalPersonnel)
                .typeCounts(typeCounts)
                .vehicles(inBounds)
                .build();
    }
}
