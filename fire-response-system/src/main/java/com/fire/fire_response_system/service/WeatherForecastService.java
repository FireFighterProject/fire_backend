package com.fire.fire_response_system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fire.fire_response_system.dto.weather.DayForecast;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class WeatherForecastService {

    private static final int[] BASE_HOURS = {2, 5, 8, 11, 14, 17, 20, 23};
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final WeatherService weatherService;
    private final ObjectMapper objectMapper;

    public List<DayForecast> getShortForecast(int nx, int ny) {
        Map.Entry<String, String> baseDT = resolveBaseDateAndTime(LocalDateTime.now());
        String json = weatherService.getVillageForecast(baseDT.getKey(), baseDT.getValue(), nx, ny, 1, 1000);
        return parseShortForecast(json);
    }

    private Map.Entry<String, String> resolveBaseDateAndTime(LocalDateTime now) {
        LocalDateTime adjusted = now.minusMinutes(10);
        int hour = adjusted.getHour();
        int selectedHour = -1;
        for (int bt : BASE_HOURS) {
            if (hour >= bt) selectedHour = bt;
        }
        if (selectedHour == -1) {
            return Map.entry(adjusted.minusDays(1).format(DATE_FMT), "2300");
        }
        return Map.entry(adjusted.format(DATE_FMT), String.format("%02d00", selectedHour));
    }

    private List<DayForecast> parseShortForecast(String json) {
        try {
            JsonNode items = objectMapper.readTree(json)
                    .path("response").path("body").path("items").path("item");

            Map<String, Map<String, String>> byDate = new LinkedHashMap<>();

            for (JsonNode item : items) {
                String date = item.get("fcstDate").asText();
                String category = item.get("category").asText();
                String value = item.get("fcstValue").asText();

                Map<String, String> dayData = byDate.computeIfAbsent(date, k -> new HashMap<>());

                switch (category) {
                    case "TMN", "TMX" -> dayData.put(category, value);
                    case "SKY" -> {
                        String cur = dayData.get("SKY");
                        if (cur == null || Integer.parseInt(value) > Integer.parseInt(cur))
                            dayData.put("SKY", value);
                    }
                    case "POP" -> {
                        String cur = dayData.get("POP");
                        if (cur == null || Integer.parseInt(value) > Integer.parseInt(cur))
                            dayData.put("POP", value);
                    }
                    case "PTY" -> {
                        String cur = dayData.get("PTY");
                        if (cur == null || (!"0".equals(value) && "0".equals(cur)))
                            dayData.put("PTY", value);
                    }
                    case "TMP" -> dayData.putIfAbsent("TMP", value);
                }
            }

            List<DayForecast> result = new ArrayList<>();
            for (Map.Entry<String, Map<String, String>> entry : byDate.entrySet()) {
                String raw = entry.getKey();
                Map<String, String> d = entry.getValue();
                result.add(DayForecast.builder()
                        .date(raw.substring(0, 4) + "-" + raw.substring(4, 6) + "-" + raw.substring(6, 8))
                        .minTemp(d.getOrDefault("TMN", d.getOrDefault("TMP", "-")))
                        .maxTemp(d.getOrDefault("TMX", d.getOrDefault("TMP", "-")))
                        .sky(skyDesc(d.getOrDefault("SKY", "")))
                        .pop(d.getOrDefault("POP", "-"))
                        .pty(ptyDesc(d.getOrDefault("PTY", "0")))
                        .build());
            }
            return result;

        } catch (Exception e) {
            throw new RuntimeException("날씨 데이터 파싱 실패: " + e.getMessage(), e);
        }
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
            case "0" -> "없음";
            case "1" -> "비";
            case "2" -> "비/눈";
            case "3" -> "눈";
            case "4" -> "소나기";
            default -> "-";
        };
    }
}
