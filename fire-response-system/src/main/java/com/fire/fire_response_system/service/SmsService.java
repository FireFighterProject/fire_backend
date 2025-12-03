package com.fire.fire_response_system.service;

import com.fire.fire_response_system.config.SolapiProperties;
import com.fire.fire_response_system.domain.vehicle.Vehicle;
import com.fire.fire_response_system.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SmsService {

    private final SolapiProperties props;
    private final VehicleRepository vehicleRepository;

    private WebClient client() {
        return WebClient.builder()
                .baseUrl("https://api.solapi.com")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    /**
     * 차량 ID → PS-LTE 번호 기반으로 문자 발송
     */
    public void sendToVehicle(Long vehicleId, String text) {

        Vehicle v = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("차량 없음: ID=" + vehicleId));

        String rawPhone = v.getPsLteNumber();
        if (rawPhone == null || rawPhone.trim().isEmpty()) {
            throw new IllegalArgumentException("PS-LTE 번호 없음");
        }

        // 010-1234-5678 → 01012345678
        String to = rawPhone.replace("-", "").trim();
        log.warn("문자 발송 대상 번호 → {}", to);

        sendSms(to, text);
    }

    /**
     * 단일 번호 문자 발송
     */
    public void sendSms(String to, String text) {

        String apiKey = props.getApiKey();
        String apiSecret = props.getApiSecret();

        log.warn("[DEBUG] 문자 발송 시도: to={}, from={}, text={}", to, props.getFrom(), text);
        log.warn("[DEBUG] API KEY={}, SECRET(길이)={}", apiKey, apiSecret != null ? apiSecret.length() : null);

        // Body JSON
        String body = """
                {
                  "message": {
                    "to": "%s",
                    "from": "%s",
                    "text": "%s"
                  }
                }
                """.formatted(to, props.getFrom(), text);

        log.warn("[DEBUG] Request Body = {}", body);

        try {
            String authHeader = createAuthHeader(apiKey, apiSecret);
            log.warn("[DEBUG] Authorization 헤더 = {}", authHeader);

            String response = client()
                    .post()
                    .uri("/messages/v4/send")
                    .header("Authorization", authHeader)
                    .bodyValue(body)
                    .retrieve()
                    .onStatus(
                            status -> !status.is2xxSuccessful(),
                            err -> err.bodyToMono(String.class).map(errorBody -> {
                                log.error("[SOLAPI ERROR BODY] → {}", errorBody);
                                throw new RuntimeException("SOLAPI 문자 발송 실패: " + errorBody);
                            })
                    )
                    .bodyToMono(String.class)
                    .block();

            log.info("SOLAPI 문자 발송 성공 → {}", response);

        } catch (Exception e) {
            log.error("SOLAPI 문자 발송 실패 (예외) → {}", e.getMessage());
            throw new RuntimeException("SOLAPI 문자 발송 실패", e);
        }

    }


    private String createAuthHeader(String apiKey, String apiSecret) throws Exception {
        // 1) 시간 (ISO 8601)
        String dateTime = Instant.now().toString();

        // 2) 랜덤 salt
        String salt = UUID.randomUUID().toString().replace("-", "");

        // 3) signature = HMAC-SHA256(apiSecret, dateTime + salt)
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hash = mac.doFinal((dateTime + salt).getBytes(StandardCharsets.UTF_8));
        String signature = HexFormat.of().formatHex(hash);

        // 4) Authorization 헤더 문자열 조합
        return "HMAC-SHA256 apiKey=%s, date=%s, salt=%s, signature=%s"
                .formatted(apiKey, dateTime, salt, signature);
    }
}
