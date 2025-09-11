package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.activity.ActivityLog;
import com.fire.fire_response_system.dto.activity.*;
import com.fire.fire_response_system.dto.common.MessageResponse;
import com.fire.fire_response_system.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActivityLogService {

    private final ActivityLogRepository logRepo;

    @Transactional
    public Long start(ActivityStartRequest req) {
        ActivityLog log = ActivityLog.builder()
                .vehicleId(req.getVehicleId())
                .stationId(req.getStationId())
                .place(req.getPlace())
                .description(req.getDescription())
                .status(1) // 활동
                .startedAt(LocalDateTime.now())
                .build();
        return logRepo.save(log).getId();
    }

    @Transactional
    public MessageResponse returnVehicle(Long id) {
        ActivityLog log = logRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("activity log not found: " + id));
        log.setStatus(2);
        log.setReturnedAt(LocalDateTime.now());
        return new MessageResponse("복귀 처리 완료");
    }

    @Transactional
    public MessageResponse move(Long id, ActivityMoveRequest req) {
        ActivityLog log = logRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("activity log not found: " + id));
        log.setPlace(req.getNewPlace());
        log.setDescription(req.getNewDescription());
        log.setMovedAt(LocalDateTime.now());
        return new MessageResponse("장소 이동 처리 완료");
    }

    @Transactional(readOnly = true)
    public List<ActivityLogResponse> list() {
        return logRepo.findAll().stream().map(ActivityLogResponse::from).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ActivityLogResponse detail(Long id) {
        ActivityLog log = logRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("activity log not found: " + id));
        return ActivityLogResponse.from(log);
    }
}