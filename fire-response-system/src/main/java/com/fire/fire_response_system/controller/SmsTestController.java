package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.sms.SmsSendRequest;
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
     * 기존 GET 방식 유지
     * GET /api/sms/to-vehicle?vehicleId=1&text=테스트
     */
    @GetMapping("/to-vehicle")
    public ResponseEntity<?> sendToVehicleGet(
            @RequestParam Long vehicleId,
            @RequestParam String text
    ) {
        smsService.sendToVehicle(vehicleId, text);
        return ResponseEntity.ok("문자 발송 완료 (GET)");
    }


    /**
     * 새로 추가된 POST 방식
     * POST /api/sms/to-vehicle
     * {
     *   "vehicleId": 60,
     *   "text": "출동요청\n링크: https://..."
     * }
     */
    @PostMapping("/to-vehicle")
    public ResponseEntity<?> sendToVehiclePost(@RequestBody SmsSendRequest req) {
        smsService.sendToVehicle(req.getVehicleId(), req.getText());
        return ResponseEntity.ok("문자 발송 완료 (POST)");
    }
}
