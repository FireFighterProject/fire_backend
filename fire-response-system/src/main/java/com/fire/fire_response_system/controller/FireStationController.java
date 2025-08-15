package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.domain.FireStation;
import com.fire.fire_response_system.service.FireStationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "기준정보 - 소방서")
@RestController
@RequestMapping("/api/fire-stations")
public class FireStationController {

    private final FireStationService service;

    // 명시적 생성자 주입
    public FireStationController(FireStationService service) {
        this.service = service;
    }

    @Operation(summary = "소방서 목록 조회",
            description = "시/도(sido) 필터(Optional)를 적용해 소방서 목록을 조회한다.")
    @GetMapping
    public List<FireStation> list(@RequestParam(required = false) String sido) {
        return service.findAll(sido);
    }
}
