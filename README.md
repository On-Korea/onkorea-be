# OnKorea Backend

## 기술 스택

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security + OAuth2 + JWT**
- **Spring Data JPA**
- **MySQL 8.0**
- **Lombok**
- **Swagger/OpenAPI**

## 주요 기능

### 1. 인증 시스템
- **이메일/비밀번호 기반 회원가입 및 로그인**
- **JWT 토큰 기반 인증**
- Google OAuth2 소셜 로그인 지원
- 신규 사용자 자동 가입

### 2. 정착난이도 스코어맵 API
- **서울시 25개 자치구별 정착난이도 점수 제공**
- 6개 카테고리별 점수 (주택, 인구, 안전, 교육, 생활편의, 복지문화)
- 사용자 맞춤형 지표 선택 및 점수 계산
- CSV 데이터 기반 통계 정보 제공

### 3. 사용자 관리 API
- 사용자 프로필 조회 및 수정
- 언어 설정 및 생일 정보 관리

### 4. REST API
- `/api/v1/auth` - 인증 API
- `/api/v1/settlement` - 정착난이도 스코어맵 API
- `/api/v1/users` - 사용자 관리 API
- Swagger UI 제공


## API 문서

- **Swagger UI**: http://localhost:8080/swagger.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 주요 엔드포인트

### 인증 API (`/api/v1/auth`)
- `POST /api/v1/auth/signup` - 이메일/비밀번호 회원가입
- `POST /api/v1/auth/login` - 이메일/비밀번호 로그인
- `GET /oauth2/authorization/google` - Google OAuth2 로그인

**회원가입 요청 예시:**
```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동"
}
```

**로그인 응답 예시:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### 정착난이도 스코어맵 API (`/api/v1/settlement`)
- `GET /api/v1/settlement` - 전체 자치구 점수 조회
- `GET /api/v1/settlement/category/{category}` - 카테고리별 자치구 점수 조회
- `GET /api/v1/settlement/district/{district}` - 특정 자치구 상세 점수 조회
- `GET /api/v1/settlement/categories` - 사용 가능한 카테고리 목록 조회
- `GET /api/v1/settlement/districts` - 서울시 25개 자치구 목록 조회
- `GET /api/v1/settlement/indicators` - 지표 목록 조회
- `POST /api/v1/settlement/custom-score` - 맞춤형 점수 계산

**맞춤형 점수 계산 요청 예시:**
```json
{
  "indicators": [
    "HOUSING_PRICE",
    "SAFETY_CRIME_RATE",
    "EDUCATION_SCHOOL_COUNT"
  ]
}
```

### 사용자 관리 API (`/api/v1/users`)
- `GET /api/v1/users/me` - 내 프로필 조회
- `PATCH /api/v1/users/me/profile` - 내 프로필 수정

**프로필 수정 요청 예시:**
```json
{
  "language": "ko",
  "birthday": "1990-01-01"
}
```

## 프로젝트 구조

```
src/main/java/com/example/onkoreabe/
├── OnkoreaBeApplication.java           # 메인 애플리케이션
├── auth/
│   ├── AuthController.java            # 인증 API 컨트롤러
│   ├── AuthService.java               # 인증 서비스
│   ├── CustomOAuth2UserService.java   # OAuth2 사용자 서비스
│   ├── jwt/
│   │   ├── JwtTokenProvider.java      # JWT 토큰 생성/검증
│   │   └── JwtAuthenticationFilter.java  # JWT 인증 필터
│   └── dto/
│       ├── LoginRequest.java          # 로그인 요청 DTO
│       ├── SignupRequest.java         # 회원가입 요청 DTO
│       └── AuthResponse.java          # 인증 응답 DTO (토큰)
├── config/
│   ├── SecurityConfig.java            # Spring Security 설정
│   └── SwaggerConfig.java             # Swagger 설정
├── user/
│   ├── User.java                      # 사용자 엔티티
│   ├── Role.java                      # 권한 Enum
│   ├── LoginType.java                 # 로그인 타입 Enum (EMAIL, GOOGLE)
│   ├── UserRepository.java            # 사용자 리포지토리
│   ├── UserService.java               # 사용자 서비스
│   ├── UserController.java            # 사용자 API 컨트롤러
│   └── dto/
│       ├── UserResponse.java          # 사용자 응답 DTO
│       └── ProfileUpdateRequest.java  # 프로필 수정 요청 DTO
└── settlement/
    ├── api/
    │   └── SettlementController.java  # 정착난이도 API 컨트롤러
    ├── dto/
    │   ├── SettlementMapResponse.java # 전체 스코어맵 응답
    │   ├── DistrictScoreResponse.java # 자치구 상세 점수 응답
    │   ├── CategoryScoreDto.java      # 카테고리별 점수 DTO
    │   ├── CustomScoreRequest.java    # 맞춤 점수 계산 요청
    │   └── CustomScoreResponse.java   # 맞춤 점수 계산 응답
    ├── score/
    │   ├── entity/
    │   │   ├── DistrictScore.java     # 자치구 점수 엔티티
    │   │   ├── ScoreCategory.java     # 점수 카테고리 Enum
    │   │   ├── SeoulDistrict.java     # 서울 자치구 Enum
    │   │   └── SettlementIndicator.java  # 정착 지표 Enum
    │   ├── repository/
    │   │   └── DistrictScoreRepository.java
    │   └── service/
    │       ├── SettlementService.java    # 정착난이도 서비스
    │       └── CustomScoreCalculator.java # 맞춤 점수 계산
    └── rawdata/
        ├── entity/
        │   └── DistrictRawData.java   # 원시 데이터 엔티티
        ├── repository/
        │   └── DistrictRawDataRepository.java
        └── service/
            └── RawDataImportService.java  # CSV 데이터 임포트
```

## 문제 해결

### MySQL 연결 오류
```
com.mysql.cj.jdbc.exceptions.CommunicationsException
```
- MySQL이 실행 중인지 확인
- 포트 번호 확인 (기본값: 3309)
- 방화벽 설정 확인
- Docker Compose로 MySQL 실행: `docker-compose up -d db`

### JWT 인증 오류
```
401 Unauthorized
```
- JWT 토큰이 요청 헤더에 포함되어 있는지 확인
- 헤더 형식: `Authorization: Bearer {token}`
- 토큰이 만료되지 않았는지 확인 (만료시간: 24시간)
- `JWT_SECRET` 환경변수가 올바르게 설정되었는지 확인

### OAuth2 인증 오류
```
invalid_client
```
- Google Cloud Console에서 Client ID/Secret 재확인
- 리디렉션 URI가 정확히 등록되었는지 확인
- 환경변수가 올바르게 설정되었는지 확인

### CSV 데이터 임포트 오류
- `src/main/resources/data/` 디렉토리에 CSV 파일이 있는지 확인
- CSV 파일 인코딩 확인 (UTF-8 권장)
- 애플리케이션 시작 시 자동으로 데이터 임포트 실행

## 개발 상태

✅ 완료된 기능:
- 이메일/비밀번호 기반 회원가입 및 로그인
- JWT 토큰 인증
- Google OAuth2 소셜 로그인
- 사용자 프로필 관리
- 정착난이도 스코어맵 API
- 카테고리별 점수 조회
- 맞춤형 점수 계산
- CSV 데이터 임포트
- Swagger API 문서화

⚠️ TODO:
- CORS 설정
- Rate Limiting 구현
- 로깅 개선
- 캐싱 전략 수립
- API 응답 페이징 처리
