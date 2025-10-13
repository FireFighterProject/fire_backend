package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.station.Station;
import com.fire.fire_response_system.dto.station.StationCreateRequest;
import com.fire.fire_response_system.dto.station.StationResponse;
import com.fire.fire_response_system.repository.StationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StationService {

    private final StationRepository stationRepository;

    /**
     * 소방서 목록 조회
     * sido가 null 또는 공백이면 전체 반환, 아니면 해당 시도만 반환
     */
    @Transactional(readOnly = true)
    public List<StationResponse> list(String sido) {
        var stations = (sido == null || sido.isBlank())
                ? stationRepository.findAll()
                : stationRepository.findBySido(sido);

        return stations.stream()
                .map(s -> new StationResponse(
                        s.getId(),
                        s.getSido(),
                        s.getName(),
                        s.getAddress()
                ))
                .toList();
    }

    /**
     * 소방서 등록
     * sido + name 기준으로 중복 방지
     */
    @Transactional
    public StationResponse create(StationCreateRequest req) {
        if (req.getSido() == null || req.getSido().isBlank()) {
            throw new IllegalArgumentException("시도(sido)는 필수 입력값입니다.");
        }
        if (req.getName() == null || req.getName().isBlank()) {
            throw new IllegalArgumentException("소방서명(name)은 필수 입력값입니다.");
        }

        boolean dup = stationRepository.existsBySidoAndName(req.getSido(), req.getName());
        if (dup) {
            throw new IllegalStateException(
                    "이미 존재하는 소방서입니다: " + req.getSido() + " " + req.getName()
            );
        }

        Station saved = stationRepository.save(
                Station.builder()
                        .sido(req.getSido())
                        .name(req.getName())
                        .address(req.getAddress())
                        .build()
        );

        return new StationResponse(
                saved.getId(),
                saved.getSido(),
                saved.getName(),
                saved.getAddress()
        );
    }
}
