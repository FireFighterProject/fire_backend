package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsTestController {

    private final SmsService smsService;

    /**
     * 차량 ID 기반 문자 발송 테스트
     * GET /api/sms/to-vehicle?vehicleId=1&text=테스트
     */
    @GetMapping("/to-vehicle")
    public ResponseEntity<?> sendToVehicle(
            @RequestParam Long vehicleId,
            @RequestParam String text
    ) {
        smsService.sendToVehicle(vehicleId, text);
        return ResponseEntity.ok("문자 발송 완료 (차량 ID 기반)");
    }
}
