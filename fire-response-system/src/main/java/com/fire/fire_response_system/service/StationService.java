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

    public List<StationResponse> list() {
        return stationRepository.findAll().stream()
                .map(s -> new StationResponse(s.getId(), s.getSido(), s.getName(), s.getAddress()))
                .toList();
    }

    @Transactional
    public StationResponse create(StationCreateRequest req) {
        // sido가 비어있으면 name만, 있으면 sido+name으로 중복 방지
        boolean dup = (req.getSido() == null || req.getSido().isBlank())
                ? stationRepository.existsByName(req.getName())
                : stationRepository.existsBySidoAndName(req.getSido(), req.getName());
        if (dup) throw new IllegalStateException("이미 존재하는 소방서 이름입니다.");

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
