# 소방 출동 관리 시스템 — 버그 수정 이력

> 프로젝트: 경상북도 소방 출동 관리 시스템 (Fire Response System)
> 기술 스택: Spring Boot 3.2.5, Java 17, JPA/Hibernate, MySQL 8.0, Docker, Nginx

---

## 1. 데이터 조회 — `IncorrectResultSizeDataAccessException` (심각)

### 원인
`getCurrentDispatchByVehicle()` 에서 `findByVehicleId(vehicleId)`를 사용했는데,
한 차량이 여러 출동명령에 배치된 이력이 있으면 복수의 레코드가 반환되어 예외 발생.

```java
// 문제 코드
Optional<DispatchVehicleMap> findByVehicleId(Long vehicleId);
// → 결과가 2개 이상이면 NonUniqueResultException 발생
```

### 해결
ENDED 상태가 아닌 활성 배치만 필터링하는 JPQL 쿼리로 교체 + 반환 타입을 `List`로 변경.

```java
@Query("""
    SELECT m FROM DispatchVehicleMap m
    JOIN m.assignment a
    JOIN a.order o
    WHERE m.vehicle.id = :vehicleId
    AND o.status != :endedStatus
    ORDER BY m.id DESC
""")
List<DispatchVehicleMap> findActiveMaps(
        @Param("vehicleId") Long vehicleId,
        @Param("endedStatus") DispatchStatus endedStatus
);
```

### 영향 범위
- `DispatchVehicleMapRepository`
- `DispatchOrderService.getCurrentDispatchByVehicle()`

---

## 2. JPA Dirty Checking 미활용 — 불필요한 `save()` 호출 (중간)

### 원인
`@Transactional` 트랜잭션 안에서 영속 상태(Managed) 엔티티를 수정한 뒤
`vehicleRepo.save(v)` 를 명시적으로 호출 — JPA dirty checking 이 자동으로 처리하므로 중복.

```java
// 문제 코드 (assignVehicles, returnVehicles 내부)
v.setStatus(1);
vehicleRepo.save(v); // 불필요 — flush 시 자동 반영됨
```

### 해결
불필요한 `save()` 호출 제거. 트랜잭션 커밋 시 dirty checking이 자동으로 UPDATE 쿼리 실행.

### 영향 범위
- `DispatchOrderService.assignVehicles()`
- `DispatchOrderService.returnVehicles()`

---

## 3. Station 엔티티 타임스탬프 미설정 (중간)

### 원인
`Station` 엔티티에 `@PrePersist` / `@PreUpdate` 훅이 없어 `created_at`, `updated_at`
컬럼이 항상 `NULL`로 저장됨.

```java
// 문제 코드 — 훅 없음, 타임스탬프 컬럼 NULL
@Column(name = "created_at", updatable = false)
private LocalDateTime createdAt;
```

### 해결
`@PrePersist` / `@PreUpdate` 추가 및 `updatable = false` 적용.

```java
@PrePersist
public void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
}

@PreUpdate
public void onUpdate() {
    this.updatedAt = LocalDateTime.now();
}
```

### 영향 범위
- `Station.java`

---

## 4. 타임스탬프 이중 설정 (중간)

### 원인
`Vehicle` 엔티티에 이미 `@PrePersist`로 타임스탬프를 설정하는데,
`VehiclesService`의 `create()` / `registerBatch()` 에서도 수동으로 같은 값을 지정.
엔티티와 서비스 레이어 간 책임이 중복.

```java
// 문제 코드 — 서비스에서 중복 설정
vehicle.setCreatedAt(LocalDateTime.now()); // 이미 @PrePersist가 처리함
vehicle.setUpdatedAt(LocalDateTime.now());
```

### 해결
서비스 레이어의 수동 타임스탬프 설정 코드 제거. 엔티티 콜백에 책임 일원화.

### 영향 범위
- `VehiclesService.create()`
- `VehiclesService.registerBatch()`

---

## 5. SMS 발송 시 잘못된 전화번호 필드 사용 (심각)

### 원인
`SmsService`에서 문자 발송 대상 전화번호를 `vehicle.getAvlNumber()` (AVL 장치 식별번호)에서
가져오고 있었음. 실제 연락처와 전혀 다른 필드를 사용한 것.

```java
// 문제 코드
String to = vehicle.getAvlNumber(); // AVL 번호 ≠ 전화번호
```

### 해결
차량 데이터 구조에 `phoneNumber` 필드를 신설하고, SMS 발송 대상을 해당 필드로 변경.

```java
// 수정 코드
String to = vehicle.getPhoneNumber();
if (to == null || to.isBlank()) {
    throw new IllegalArgumentException("차량 연락처가 없습니다.");
}
```

### 영향 범위
- `Vehicle.java` — `phoneNumber` 필드 추가
- `SmsService.java`
- `VehicleCreateRequest`, `VehicleBatchRequest`, `VehicleUpdateRequest`
- `VehicleResponse`, `VehicleListItem`, `VehicleSummary`

---

## 6. 출동명령 생성 요청 입력값 검증 누락 (중간)

### 원인
`CreateDispatchOrderRequest` DTO에 `@NotBlank` 검증 애노테이션이 없어
`title`, `address`, `content` 가 공백 또는 null인 채로 DB에 INSERT 시도 가능.
`NOT NULL` 제약이 있는 컬럼에 null이 삽입되면 `DataIntegrityViolationException` 발생.

```java
// 문제 코드 — 검증 없음
private String title;
private String address;
private String content;
```

### 해결
`@NotBlank` 추가 및 컨트롤러에 `@Valid` 적용.

```java
@NotBlank(message = "title 필수")
private String title;
@NotBlank(message = "address 필수")
private String address;
@NotBlank(message = "content 필수")
private String content;
```

### 영향 범위
- `CreateDispatchOrderRequest.java`
- `DispatchOrderController.createOrder()`

---

## 7. 차량 등록 시 소방서 조회 로직 오류 (중간)

### 원인
차량 등록 시 `findBySidoAndName(sido, stationName)` 으로 소방서를 조회했는데,
클라이언트가 전송하는 `callSign` 형식이 변경되어 `sido` 필드가 요청에 포함되지 않게 됨.
소방서를 찾지 못해 차량 등록 실패.

### 해결
`sido` 를 요청에서 제거하고, `stationName` 만으로 소방서를 조회 (`findFirstByName`).
`sido` 는 조회된 소방서 엔티티에서 파생.

```java
// 수정 코드
Station station = stationRepo.findFirstByName(req.getStationName())
        .orElseThrow(() -> new EntityNotFoundException("소방서 없음: " + req.getStationName()));

String sido = station.getSido(); // 소방서에서 파생
```

### 영향 범위
- `StationRepository.java` — `findFirstByName()` 추가
- `VehiclesService.create()`, `registerBatch()`
- `VehicleCreateRequest`, `VehicleBatchRequest` — `sido` 필드 제거

---

## 8. 전역 예외 핸들러 미비 (중간)

### 원인
`@RestControllerAdvice` 에 `404 Not Found` 케이스 (`EntityNotFoundException`, `NoSuchElementException`) 와
예상치 못한 서버 에러에 대한 핸들러가 없어 클라이언트가 의미 없는 500 HTML 응답을 받음.

### 해결
누락된 예외 케이스 핸들러 추가 및 모든 반환 타입을 `Map<String, String>` JSON으로 통일.

```java
@ExceptionHandler({EntityNotFoundException.class, NoSuchElementException.class})
public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException e) {
    return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
}

@ExceptionHandler(Exception.class)
public ResponseEntity<Map<String, String>> handleGeneral(Exception e) {
    return ResponseEntity.status(500).body(Map.of("message", "서버 내부 오류"));
}
```

### 영향 범위
- `ApiExceptionHandler.java`

---

## 9. 컨트롤러 비타입 `ResponseEntity<?>` 사용 (경미)

### 원인
여러 컨트롤러에서 반환 타입을 `ResponseEntity<?>` 로 선언. 컴파일 타임 타입 검증이
불가능하고 Swagger 문서 자동 생성 시 응답 스키마가 누락됨.

### 해결
모든 컨트롤러 메서드의 반환 타입을 구체적인 타입으로 명시.

```java
// 수정 전
ResponseEntity<?> listOrders()

// 수정 후
ResponseEntity<List<DispatchOrderListItem>> listOrders()
```

### 영향 범위
- `DispatchOrderController`, `GpsController`, `SmsController`

---

## 10. 미사용 DTO 방치 (경미)

### 원인
`DispatchOrderSummary.java` DTO가 코드 어디에서도 참조되지 않는 데드 코드로 존재.

### 해결
파일 삭제.

### 영향 범위
- `dto/dispatch/DispatchOrderSummary.java` 삭제

---

## 11. 한글 URL 쿼리 파라미터로 인한 HTTP 400 오류 (심각)

### 원인
외부 클라이언트가 `GET /api/fire-stations?sido=경상북도` 요청 시 한글을 URL 인코딩 없이
raw 바이트(EUC-KR 또는 UTF-8)로 전송. Tomcat 10.1이 RFC 7230 기준으로 비 ASCII 바이트를
HTTP 파싱 단계에서 거부 → Spring 레이어 도달 전에 400 반환.

```
IllegalArgumentException: Invalid character found in the request target
[/api/fire-stations?sido=0xba0xce0xbb0xea...]
```

### 해결
Nginx 리버스 프록시를 도입하여 `/api/fire-stations` 엔드포인트 요청은 쿼리스트링을
제거한 뒤 Spring Boot로 전달. DB에 경상북도 데이터만 존재하므로 `sido` 필터 없이
전체 반환 = 필터 결과와 동일.

```nginx
# nginx.conf
location = /api/fire-stations {
    proxy_pass http://app:8081/api/fire-stations;  # 쿼리스트링 미전달
}

location / {
    proxy_pass http://app:8081;
}
```

서비스 레이어에도 `sido` 필터 결과가 비어있을 경우 전체 반환하는 fallback 추가.

```java
stations = stationRepository.findBySido(sido);
if (stations.isEmpty()) {
    stations = stationRepository.findAll();
}
```

### 영향 범위
- `docker-compose.yml` — Nginx 서비스 추가
- `nginx.conf` 신규 생성
- `StationService.list()`

---

## 12. 배포 환경 구성 부재 (인프라)

### 원인
개발 환경에서만 실행 가능한 구조로, 운영 배포를 위한 컨테이너화가 되어 있지 않았음.

### 해결
- **Dockerfile**: 멀티 스테이지 빌드 (`jdk-alpine` 빌드 → `jre-alpine` 실행)로 이미지 경량화
- **docker-compose.yml**: Nginx + Spring Boot 두 컨테이너 오케스트레이션
- **`.env`**: 민감 정보(DB 접속 정보, API 키) 분리 및 `.gitignore` 처리
- **AWS EC2**: 별도 DB 서버 신설 (3.36.103.212), `ddl-auto: update` 로 초기 스키마 자동 생성

### 최종 아키텍처

```
클라이언트 (포트 8080)
       ↓
  [Nginx 컨테이너]  — 라우팅 / 한글 파라미터 처리
       ↓
  [Spring Boot 컨테이너 : 8081]  — 비즈니스 로직
       ↓
  [AWS RDS / EC2 MySQL : 3306]  — 데이터 저장
```

---

## 수정 요약

| # | 분류 | 심각도 | 핵심 내용 |
|---|------|--------|-----------|
| 1 | 데이터 조회 | 심각 | `NonUniqueResultException` → JPQL + `List` 반환으로 해결 |
| 2 | JPA | 중간 | 불필요한 `save()` 제거 — dirty checking 활용 |
| 3 | 엔티티 | 중간 | `Station` 타임스탬프 `@PrePersist` 누락 추가 |
| 4 | 레이어 책임 | 중간 | 서비스-엔티티 이중 타임스탬프 제거 |
| 5 | 비즈니스 로직 | 심각 | SMS 전화번호 필드 오용 (`avlNumber` → `phoneNumber`) |
| 6 | 유효성 검사 | 중간 | 출동명령 요청 `@NotBlank` 누락 추가 |
| 7 | 비즈니스 로직 | 중간 | 소방서 조회 로직 재설계 (`sido` 제거, 소방서에서 파생) |
| 8 | 예외 처리 | 중간 | 전역 핸들러 404 / 500 케이스 추가 |
| 9 | 코드 품질 | 경미 | `ResponseEntity<?>` → 구체 타입 명시 |
| 10 | 코드 품질 | 경미 | 미사용 DTO `DispatchOrderSummary` 삭제 |
| 11 | 인프라 | 심각 | 한글 URL 파라미터 → Nginx 프록시로 우회 |
| 12 | 인프라 | - | Docker 컨테이너화 및 AWS EC2 배포 환경 구성 |
