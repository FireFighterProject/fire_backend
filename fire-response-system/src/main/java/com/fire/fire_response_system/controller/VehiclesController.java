package com.fire.fire_response_system.controller;

import com.fire.fire_response_system.dto.vehicle.*;
import com.fire.fire_response_system.service.VehicleQueryService;
import com.fire.fire_response_system.service.VehiclesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(
        name = "Vehicle",
        description = "차량 기본 정보 등록/조회 및 상태·집결지 관리 API"
)
public class VehiclesController {

    private final VehicleQueryService vehicleQueryService;
    private final VehiclesService vehiclesService;

    // -------------------------------
    // 1) 차종 코드 목록 조회
    // -------------------------------
    @GetMapping("/vehicle-types")
    @Operation(
            summary = "차종 코드 목록 조회",
            description = """
                    차량 등록/수정 화면에서 사용되는 차종(type_name) 목록을 조회합니다.<br>
                    - 예시: 경펌, 소펌, 중펌, 대펌, 중형탱크, 대형탱크, 급수탱크, 화학, 구조, 구급, 지휘, 고가, 로젠바우어 등<br>
                    - 정적 코드 또는 코드 테이블 기준으로 조회됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차종 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)))
    })
    public ResponseEntity<List<String>> getVehicleTypes() {
        return ResponseEntity.ok(vehicleQueryService.getVehicleTypes());
    }

    // -------------------------------
    // 2) 차량 단건 등록
    // -------------------------------
    @PostMapping("/vehicles")
    @Operation(
            summary = "차량 단건 등록",
            description = """
            신규 차량 1대를 등록합니다.<br><br>

             **소방서 매핑 규칙(stationName 기반)**<br>
            - 요청에서는 stationName(소방서명)만 전달합니다.<br>
            - 백엔드에서 stationName + sido 로 stations 테이블에서 stationId 를 자동 조회합니다.<br>
            - stationName 이 존재하지 않으면 400 에러를 발생합니다.<br><br>

             **중복 검증 규칙**<br>
            - 동일 stationName(→stationId) 내에서 callSign(호출명)이 중복되면 <b>409(CONFLICT)</b> 발생<br><br>

             **상태(status) 기본값**<br>
            - 요청에 status 가 없으면 기본값 0(대기)로 저장됩니다.<br><br>

             **집결지(rallyPoint) 자동 규칙**<br>
            - sido 가 '경북'이면 기본값 0<br>
            - 그 외 지역이면 기본값 1 로 자동 설정<br><br>

             **입력 항목 설명**<br>
            - stationName: 소방서명 (예: '경산소방서')<br>
            - sido: 시/도 단위 지역명 (예: '경북', '서울')<br>
            - callSign: 호출명 (예: '경산-01')<br>
            - typeName: 차종명 (예: 경펌, 소펌, 탱크, 구조, 구급 등)<br>
            - capacity: 차량 용량(L 또는 kg)<br>
            - personnel: 탑승 인원 수<br>
            - avlNumber: AVL 기기 번호<br>
            - psLteNumber: PS-LTE 번호<br>
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "차량 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "stationName 또는 필수 입력값 누락"),
            @ApiResponse(responseCode = "409", description = "동일 소방서 내 callSign 중복"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody
            @Parameter(description = "등록할 차량 정보", required = true)
            VehicleCreateRequest req
    ) {
        return ResponseEntity.status(201).body(vehiclesService.create(req));
    }


    // -------------------------------
    // 3) 차량 다건 등록 (배치)
    // -------------------------------
    @PostMapping("/vehicles/batch")
    @Operation(
            summary = "차량 다건 등록 (엑셀→JSON 변환)",
            description = """
            엑셀을 JSON으로 변환한 데이터를 기반으로 여러 차량을 한꺼번에 등록합니다.<br><br>

            **소방서 매핑 규칙(stationName 기반)**<br>
            - 각 row 에서 stationName + sido 로 stationId 를 자동 조회합니다.<br>
            - stationName 이 존재하지 않으면 해당 row 는 <b>자동 스킵</b>되고 오류 메시지가 추가됩니다.<br><br>

            **중복 처리 규칙**<br>
            - 동일 stationName 내 callSign 이 이미 존재하면 <b>등록하지 않고 스킵</b><br>
            - 스킵된 row 는 응답의 messages 배열에 기록됩니다.<br><br>

            **자동 처리 규칙**<br>
            - rallyPoint: sido = '경북' → 0, 그 외 지역 → 1<br>
            - status: 요청에 없으면 기본 0(대기)<br><br>

            **입력 예시(JSON)**<br>
            ```json
            [
              {
                "stationName": "경산소방서",
                "sido": "경북",
                "callSign": "경산-01",
                "typeName": "구조",
                "capacity": 3000,
                "personnel": 4
              },
              {
                "stationName": "강남소방서",
                "sido": "서울",
                "callSign": "강남-02",
                "typeName": "경펌",
                "capacity": 1500,
                "personnel": 3
              }
            ]
            ```<br><br>

            **응답 내용**<br>
            - total: 전체 요청 개수<br>
            - inserted: 실제 등록된 차량 수<br>
            - duplicates: callSign 중복으로 스킵된 개수<br>
            - errors: stationName 매핑 실패 또는 기타 스킵 사유 메시지 목록<br>
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "다건 등록 결과",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleBatchResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 형식 오류"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<VehicleBatchResponse> registerBatch(
            @RequestBody
            @Parameter(description = "엑셀→JSON 변환된 차량 목록", required = true)
            List<VehicleBatchRequest> requests
    ) {
        return ResponseEntity.ok(vehiclesService.registerBatch(requests));
    }


    // -------------------------------
    // 4) 차량 목록 조회
    // -------------------------------
    @GetMapping("/vehicles")
    @Operation(
            summary = "차량 목록 조회",
            description = """
                    등록된 차량 목록을 조건별로 조회합니다.<br>
                    - stationId: 특정 소방서 ID 기준으로 필터링 (예: 경산소방서만 조회)<br>
                    - status: 차량 상태로 필터링 (0=대기, 1=활동, 2=철수)<br>
                    - typeName: 차종명으로 필터링 (정확 일치)<br>
                    - callSign: 호출명 부분 검색 (LIKE 검색, 예: '강남소방서-01' 일부만 입력해도 조회)<br>
                    - 모든 파라미터는 선택사항이며, 미지정 시 전체 목록을 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차량 목록 조회 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleListItem.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 쿼리 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<List<VehicleListItem>> list(
            @RequestParam(required = false)
            @Parameter(description = "소방서 ID(stations.id). 지정 시 해당 소방서 차량만 조회")
            Long stationId,

            @RequestParam(required = false)
            @Parameter(description = "차량 상태 (0=대기, 1=활동, 2=철수)")
            Integer status,

            @RequestParam(required = false)
            @Parameter(description = "차종명 (예: 경펌, 소펌, 중형탱크 등)")
            String typeName,

            @RequestParam(required = false, name = "callSign")
            @Parameter(description = "호출명 부분 검색용 문자열 (LIKE 검색)")
            String callSignLike
    ) {
        return ResponseEntity.ok(vehiclesService.list(stationId, status, typeName, callSignLike));
    }

    // -------------------------------
    // 5) 차량 정보 수정
    // -------------------------------
    @PatchMapping("/vehicles/{id}")
    @Operation(
            summary = "차량 정보 수정",
            description = """
                    특정 차량의 기본 정보를 수정합니다.<br>
                    - path 변수 id: vehicles.id (수정 대상 차량 PK)<br>
                    - callSign을 변경하는 경우, 동일 stationId 내에서 중복 여부를 검증합니다.<br>
                    - capacity, personnel, avlNumber, psLteNumber, rallyPoint 등도 함께 변경할 수 있습니다.<br>
                    - 부분 수정(PATCH)이지만, DTO 설계에 따라 전달된 필드만 반영되도록 서비스에서 처리합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "차량 수정 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "유효하지 않은 수정 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 id의 차량을 찾을 수 없음"),
            @ApiResponse(responseCode = "409", description = "동일 소방서 내 호출명(callSign) 중복"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<VehicleResponse> update(
            @PathVariable
            @Parameter(description = "수정할 차량 ID(vehicles.id)", required = true)
            Long id,

            @RequestBody
            @Parameter(description = "수정할 필드를 포함한 요청 바디", required = true)
            VehicleUpdateRequest req
    ) {
        return ResponseEntity.ok(vehiclesService.update(id, req));
    }

    // -------------------------------
    // 6) 차량 상태 변경
    // -------------------------------
    @PatchMapping("/vehicles/{id}/status")
    @Operation(
            summary = "차량 상태 변경",
            description = """
                    특정 차량의 상태(status)를 변경합니다.<br>
                    - status 코드: 0=대기, 1=활동, 2=철수<br>
                    - 현황/출동/활동/지도/통계 페이지 집계에 직접 영향을 주는 핵심 값입니다.<br>
                    - 예시: 활동 페이지에서 복귀 처리 시 0(대기)로 변경.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "상태 변경 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "허용되지 않는 상태 코드"),
            @ApiResponse(responseCode = "404", description = "해당 id의 차량을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<VehicleResponse> updateStatus(
            @PathVariable
            @Parameter(description = "상태를 변경할 차량 ID(vehicles.id)", required = true)
            Long id,

            @Valid @RequestBody
            @Parameter(description = "변경할 상태 코드(0=대기, 1=활동, 2=철수)", required = true)
            VehicleStatusUpdateRequest req
    ) {
        return ResponseEntity.ok(vehiclesService.updateStatus(id, req.getStatus()));
    }

    // -------------------------------
    // 7) 집결지 플래그 설정/토글
    // -------------------------------
    @PatchMapping("/vehicles/{id}/assembly")
    @Operation(
            summary = "집결지 플래그 토글/설정",
            description = """
                    특정 차량의 집결지 표시(rallyPoint)를 변경합니다.<br>
                    - 요청 바디가 없으면(null) 현재 값을 기준으로 토글합니다. (0 → 1, 1 → 0)<br>
                    - 요청 바디에 rallyPoint(0 또는 1)를 전달하면 해당 값으로 강제 설정합니다.<br>
                    - 평상시/재난시 현황 집계에서 '자원 집결지' 여부 판단에 사용됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "집결지 설정/토글 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "rallyPoint 값이 0/1 이외이거나 잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "해당 id의 차량을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<VehicleResponse> updateAssembly(
            @PathVariable
            @Parameter(description = "집결지 설정을 변경할 차량 ID(vehicles.id)", required = true)
            Long id,

            @RequestBody(required = false)
            @Parameter(description = "집결지 플래그(0 또는 1). 미전달 시 토글 동작", required = false)
            VehicleAssemblyUpdateRequest req
    ) {
        Integer value = (req == null) ? null : req.getRallyPoint();
        return ResponseEntity.ok(vehiclesService.updateAssembly(id, value));
    }
}
