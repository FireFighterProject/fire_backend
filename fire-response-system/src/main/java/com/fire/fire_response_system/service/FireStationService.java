package com.fire.fire_response_system.service;

import com.fire.fire_response_system.domain.FireStation;
import com.fire.fire_response_system.repository.FireStationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FireStationService {

    private final FireStationRepository repository;

    // 명시적 생성자 주입
    public FireStationService(FireStationRepository repository) {
        this.repository = repository;
    }

    public List<FireStation> findAll(String sido) {
        if (sido != null && !sido.isBlank()) {
            return repository.findBySido(sido);
        }
        return repository.findAll();
    }
}
