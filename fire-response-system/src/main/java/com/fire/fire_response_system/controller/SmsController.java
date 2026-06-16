package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.sms.SmsSendRequest;
import com.fire.fire_response_system.service.SmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sms")
@RequiredArgsConstructor
public class SmsController {

    private final SmsService smsService;

    @GetMapping("/to-vehicle")
    public ResponseEntity<String> sendToVehicleGet(
            @RequestParam Long vehicleId,
            @RequestParam String text
    ) {
        smsService.sendToVehicle(vehicleId, text);
        return ResponseEntity.ok("문자 발송 완료 (GET)");
    }

    @PostMapping("/to-vehicle")
    public ResponseEntity<String> sendToVehiclePost(@RequestBody SmsSendRequest req) {
        smsService.sendToVehicle(req.getVehicleId(), req.getText());
        return ResponseEntity.ok("문자 발송 완료 (POST)");
    }
}
