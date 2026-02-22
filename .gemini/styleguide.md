# 프로젝트 코드 리뷰 스타일 가이드

이 문서는 Java Spring Boot 기반 MSA 멀티 모듈 프로젝트의 코딩 컨벤션 및 아키텍처 원칙입니다. 코드 리뷰 시 다음 항목들이 잘 준수되었는지 집중적으로 검토해 주세요.

## 1. MSA 및 멀티 모듈 아키텍처 (Architecture & Modules)
* **모듈 간 의존성:** `core`, `domain`, `api`, `infrastructure` 등 각 모듈의 역할이 명확히 분리되어야 하며, 모듈 간의 순환 참조(Circular Dependency)가 발생하지 않았는지 확인합니다.
* **서비스 간 통신:** 마이크로서비스 간 비동기 메시징을 위해 Kafka 등을 사용할 때, 프로듀서와 컨슈머의 에러 처리 로직(예: 재시도, Dead Letter Queue 처리)이 안정적으로 구현되었는지 검토합니다.

## 2. Spring Boot 모범 사례 (Spring Boot Best Practices)
* **의존성 주입 (DI):** `@Autowired` 필드 주입 대신, `final` 키워드와 생성자 주입(Constructor Injection)을 사용하여 불변성을 보장하고 테스트 용이성을 높여야 합니다. (Lombok의 `@RequiredArgsConstructor` 활용 권장)
* **예외 처리:** 개별 컨트롤러에서 `try-catch`를 남발하기보다는, `@RestControllerAdvice`와 `@ExceptionHandler`를 활용하여 전역적이고 일관된 에러 응답 구조를 유지해야 합니다.

## 3. 성능 및 효율성 (Performance & Efficiency)
* **비동기 및 논블로킹:** WebFlux와 같은 리액티브 스택을 사용하는 구간에서는 블로킹(Blocking) 호출이 발생하지 않는지, 리액티브 체인(Mono/Flux)이 올바르게 연결되고 구독(Subscribe)되는지 확인합니다.
* **데이터 구조 및 캐싱:** 대규모 트래픽 처리 시 데이터베이스 부하를 줄이기 위해 캐싱 전략이 적절히 사용되었는지 확인합니다. 데이터 존재 여부를 빠르게 파악해야 하는 경우 블룸 필터(Bloom Filter)와 같은 효율적인 자료구조의 도입 여부나 최적화 포인트를 제안해 주세요.
* **JPA/Hibernate 최적화:** N+1 문제가 발생하지 않는지(필요시 `Fetch Join` 또는 `@EntityGraph` 사용), 불필요한 영속성 컨텍스트 조회가 없는지 확인합니다.

## 4. 코딩 컨벤션 및 가독성 (Readability)
* **명명 규칙:** * 클래스는 `PascalCase`, 메서드와 변수는 `camelCase`를 사용합니다.
    * 상수는 `UPPER_SNAKE_CASE`를 사용합니다.
* **불변성 보장:** 값이 변경되지 않는 변수나 객체 상태에는 적극적으로 `final` 키워드를 사용합니다.
* **Optional 사용:** `NullPointerException` 방지를 위해 반환 타입에 `Optional`을 적절히 사용하되, 안티 패턴(예: Optional 필드 선언, 컬렉션에 Optional 사용 등)이 없는지 검토합니다.

## 5. 보안 및 로깅 (Security & Logging)
* 민감한 정보(API 키, 데이터베이스 비밀번호 등)가 코드 내에 하드코딩되지 않고 환경 변수나 설정 파일(Vault, Config Server 등)로 분리되었는지 확인합니다.
* 로깅은 `System.out.println` 대신 `Slf4j`를 사용하며, 트러블슈팅에 필요한 충분한 컨텍스트(예: 요청 ID, 사용자 식별자 등)를 적절한 로그 레벨(INFO, WARN, ERROR)로 남기고 있는지 확인합니다.