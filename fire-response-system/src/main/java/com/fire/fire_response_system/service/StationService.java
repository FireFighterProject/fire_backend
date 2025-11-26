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

    @Transactional(readOnly = true)
    public List<StationResponse> list(String sido) {
        List<Station> stations =
                (sido == null || sido.isBlank())
                        ? stationRepository.findAll()
                        : stationRepository.findBySido(sido);

        return stations.stream()
                .map(s -> new StationResponse(
                        s.getId(), s.getSido(), s.getName(), s.getAddress()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public StationResponse getOne(Long id) {
        Station s = stationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Station not found: " + id));

        return new StationResponse(s.getId(), s.getSido(), s.getName(), s.getAddress());
    }

    @Transactional
    public StationResponse create(StationCreateRequest req) {

        if (req.getSido() == null || req.getSido().isBlank())
            throw new IllegalArgumentException("시도(sido)는 필수입니다.");

        if (req.getName() == null || req.getName().isBlank())
            throw new IllegalArgumentException("소방서명(name)은 필수입니다.");

        if (stationRepository.existsBySidoAndName(req.getSido(), req.getName()))
            throw new IllegalStateException("이미 존재하는 소방서입니다.");

        Station saved = stationRepository.save(
                Station.builder()
                        .sido(req.getSido())
                        .name(req.getName())
                        .address(req.getAddress())
                        .build()
        );

        return new StationResponse(saved.getId(), saved.getSido(), saved.getName(), saved.getAddress());
    }
}
