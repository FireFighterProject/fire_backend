package com.fire.fire_response_system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherSmsService {

    private static final int[] BASE_HOURS = {2, 5, 8, 11, 14, 17, 20, 23};
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private final WeatherService weatherService;
    private final SmsService smsService;
    private final ObjectMapper objectMapper;

    public void sendWeatherForecast(Long vehicleId, int nx, int ny) {
        Map.Entry<String, String> baseDT = resolveBaseDateAndTime(LocalDateTime.now());
        String baseDate = baseDT.getKey();
        String baseTime = baseDT.getValue();

        log.info("날씨 예보 SMS 발송 요청 → vehicleId={}, nx={}, ny={}, baseDate={}, baseTime={}",
                vehicleId, nx, ny, baseDate, baseTime);

        String json = weatherService.getVillageForecast(baseDate, baseTime, nx, ny, 1, 1000);
        String text = formatWeatherSms(json);

        smsService.sendToVehicle(vehicleId, text);
        log.info("날씨 예보 SMS 발송 완료 → vehicleId={}", vehicleId);
    }

    // 기상청 발표 시각(0200~2300, 3시간 간격)에서 현재 시각 기준 가장 최근 시각 결정
    private Map.Entry<String, String> resolveBaseDateAndTime(LocalDateTime now) {
        LocalDateTime adjusted = now.minusMinutes(10); // API 반영 지연 고려
        int hour = adjusted.getHour();

        int selectedHour = -1;
        for (int bt : BASE_HOURS) {
            if (hour >= bt) selectedHour = bt;
        }

        if (selectedHour == -1) {
            // 02:10 이전 → 전날 23:00 발표분 사용
            LocalDateTime yesterday = adjusted.minusDays(1);
            return Map.entry(yesterday.format(DATE_FMT), "2300");
        }

        return Map.entry(adjusted.format(DATE_FMT), String.format("%02d00", selectedHour));
    }

    private String formatWeatherSms(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);

            String resultCode = root.path("response").path("header").path("resultCode").asText();
            if (!"00".equals(resultCode)) {
                String resultMsg = root.path("response").path("header").path("resultMsg").asText();
                throw new RuntimeException("기상청 API 오류: " + resultMsg);
            }

            JsonNode items = root.path("response").path("body").path("items").path("item");
            String nowStr = LocalDateTime.now().format(DATETIME_FMT);

            // 현재 시각 이후 가장 가까운 예보 시각 탐색
            String targetFcstDate = null;
            String targetFcstTime = null;

            for (JsonNode item : items) {
                String fcstDate = item.get("fcstDate").asText();
                String fcstTime = item.get("fcstTime").asText();
                String fcstDT = fcstDate + fcstTime;

                if (fcstDT.compareTo(nowStr) >= 0) {
                    targetFcstDate = fcstDate;
                    targetFcstTime = fcstTime;
                    break;
                }
            }

            // fallback: 첫 번째 예보 시각 사용
            if (targetFcstDate == null && items.size() > 0) {
                targetFcstDate = items.get(0).get("fcstDate").asText();
                targetFcstTime = items.get(0).get("fcstTime").asText();
            }

            if (targetFcstDate == null) {
                throw new RuntimeException("예보 데이터가 없습니다.");
            }

            // 해당 시각의 카테고리별 값 수집
            Map<String, String> data = new HashMap<>();
            for (JsonNode item : items) {
                if (targetFcstDate.equals(item.get("fcstDate").asText())
                        && targetFcstTime.equals(item.get("fcstTime").asText())) {
                    data.put(item.get("category").asText(), item.get("fcstValue").asText());
                }
            }

            return buildSmsText(data, targetFcstDate, targetFcstTime);

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("날씨 데이터 파싱 실패: " + e.getMessage(), e);
        }
    }

    private String buildSmsText(Map<String, String> data, String fcstDate, String fcstTime) {
        String date = fcstDate.substring(0, 4) + "." + fcstDate.substring(4, 6) + "." + fcstDate.substring(6, 8);
        String time = fcstTime.substring(0, 2) + ":" + fcstTime.substring(2, 4);

        String pty = data.getOrDefault("PTY", "0");
        String precipitation = "0".equals(pty) ? "없음" : ptyDesc(pty);

        return "[소방청 날씨 단기예보]\n"
                + "일시: " + date + " " + time + "\n"
                + "기온: " + data.getOrDefault("TMP", "-") + "°C\n"
                + "하늘: " + skyDesc(data.getOrDefault("SKY", "")) + "\n"
                + "강수: " + precipitation + "\n"
                + "강수확률: " + data.getOrDefault("POP", "-") + "%\n"
                + "풍속: " + data.getOrDefault("WSD", "-") + "m/s\n"
                + "습도: " + data.getOrDefault("REH", "-") + "%";
    }

    private String skyDesc(String value) {
        return switch (value) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> "-";
        };
    }

    private String ptyDesc(String value) {
        return switch (value) {
            case "1" -> "비";
            case "2" -> "비/눈";
            case "3" -> "눈";
            case "4" -> "소나기";
            default -> "-";
        };
    }
}
