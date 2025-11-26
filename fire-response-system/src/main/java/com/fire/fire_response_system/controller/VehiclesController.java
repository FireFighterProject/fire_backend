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

    @PostMapping("/vehicles")
    @Operation(
            summary = "차량 단건 등록",
            description = """
                신규 차량을 1대 등록합니다.<br><br>
               
                 **중복 검증 규칙**<br>
                - 동일한 stationId(소방서) 내에서 callSign(호출명)이 중복되면 <b>409(CONFLICT)</b> 발생<br><br>
                
                 **상태(status) 기본값**<br>
                - status는 요청으로 받지 않으며 서비스 내부에서 기본값 0(대기)로 저장됩니다.<br><br>
                
                 **집결지(rallyPoint)**<br>
                - 단건 등록에서는 rallyPoint(집결지 여부)를 입력받지 않습니다.<br>
                - 지역(sido)이 '경북'이면 기본 0, 그 외 지역이면 1로 서비스에서 자동 설정됩니다.<br><br>
                
                 **입력 항목 설명**<br>
                - stationId: 차량이 소속된 소방서의 ID(stations.id)<br>
                - sido: 시/도 단위 지역명 (예: 경상북도, 서울특별시)<br>
                - callSign: 현장에서 사용하는 호출명 (예: '강남소방서-01')<br>
                - typeName: 차종명 (경펌/소펌/중펌/대펌/중형탱크/대형탱크/구조/구급 등)<br>
                - capacity: 용량(L 또는 kg). 펌프 차량의 물 용량, 탱크 차량의 탱크 용량 등<br>
                - personnel: 기본 탑승 인원 수<br>
                - avlNumber: AVL(차량 위치 송신 단말) 전화번호 또는 기기번호<br>
                - psLteNumber: PS-LTE 번호 (재난안전통신망 번호)<br>
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "차량 등록 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = VehicleResponse.class))),
            @ApiResponse(responseCode = "400", description = "필수 값 누락 또는 잘못된 요청 형식"),
            @ApiResponse(responseCode = "409", description = "동일 stationId + callSign 차량 이미 존재"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    public ResponseEntity<VehicleResponse> create(
            @Valid @RequestBody
            @Parameter(description = "등록할 차량 정보", required = true)
            VehicleCreateRequest req
    ) {
        return ResponseEntity.status(201).body(vehiclesService.create(req));
    }

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
