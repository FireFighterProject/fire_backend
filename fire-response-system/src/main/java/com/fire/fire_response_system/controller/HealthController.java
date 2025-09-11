package com.fire.fire_response_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HealthController {

    @GetMapping("/ping")
    @Operation(summary = "헬스체크", description = "서버 살아있는지 확인")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
}
