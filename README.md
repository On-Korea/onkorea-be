# OnKorea Backend

Google OAuth2 인증과 Python AI 서버 연동 기능을 제공하는 Spring Boot 기반 백엔드 프로젝트입니다.

## 기술 스택

- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Security + OAuth2**
- **Spring Data JPA**
- **MySQL 8.0**
- **WebClient (Reactive)**
- **Lombok**
- **Swagger/OpenAPI**

## 주요 기능

### 1. Google OAuth2 소셜 로그인
- Google 계정을 통한 사용자 인증
- 신규 사용자 자동 가입
- 기존 사용자 정보 업데이트

### 2. AI 챗봇 API
- Python FastAPI 서버와 연동
- 비동기 통신 (WebClient)
- 에러 핸들링 및 타임아웃 처리
- 입력 검증

### 3. REST API
- `/api/v1/chat` - AI 챗봇 엔드포인트
- Swagger UI 제공

## 환경 설정

### 1. 환경변수 설정

프로젝트는 다음 환경변수를 지원합니다. 설정하지 않으면 기본값이 사용됩니다.

#### 데이터베이스 설정
```bash
export DB_HOST=localhost          # 기본값: localhost
export DB_PORT=3306               # 기본값: 3306
export DB_NAME=onkorea_db         # 기본값: onkorea_db
export DB_USERNAME=root           # 기본값: root
export DB_PASSWORD=your_password  # 기본값: 12345678
```

#### Google OAuth2 설정
```bash
export GOOGLE_CLIENT_ID=your_google_client_id
export GOOGLE_CLIENT_SECRET=your_google_client_secret
```

### 2. Google OAuth2 자격증명 발급

1. [Google Cloud Console](https://console.cloud.google.com/apis/credentials)에 접속
2. 새 프로젝트 생성 (또는 기존 프로젝트 선택)
3. "사용자 인증 정보" → "사용자 인증 정보 만들기" → "OAuth 클라이언트 ID" 선택
4. 애플리케이션 유형: "웹 애플리케이션"
5. 승인된 리디렉션 URI 추가:
   - `http://localhost:8080/login/oauth2/code/google`
6. 생성된 클라이언트 ID와 클라이언트 보안 비밀을 환경변수로 설정

### 3. MySQL 설정

#### Docker Compose 사용 (권장)
```bash
docker-compose up -d db
```

#### 직접 설치
```bash
# MySQL 8.0 설치 후
mysql -u root -p

CREATE DATABASE onkorea_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

## 실행 방법

### 로컬 개발 환경

```bash
# 1. 의존성 다운로드
./gradlew clean build

# 2. 환경변수 설정 후 실행
export GOOGLE_CLIENT_ID=your_client_id
export GOOGLE_CLIENT_SECRET=your_client_secret
export DB_PASSWORD=your_db_password

./gradlew bootRun
```

### Docker Compose 사용

```bash
# 전체 서비스 실행 (Spring + Python AI + MySQL)
docker-compose up -d

# 로그 확인
docker-compose logs -f spring-app
```

## API 문서

애플리케이션 실행 후 다음 URL에서 API 문서를 확인할 수 있습니다:

- **Swagger UI**: http://localhost:8080/swagger.html
- **API Docs**: http://localhost:8080/v3/api-docs

## 주요 엔드포인트

### 인증
- `GET /` - 로그인 페이지
- `GET /oauth2/authorization/google` - Google 로그인 시작
- `GET /loginsuccess` - 로그인 성공 페이지
- `POST /logout` - 로그아웃

### AI 챗봇
- `POST /api/v1/chat` - AI 질문 및 답변

**요청 예시:**
```json
{
  "question": "한국의 수도는 어디인가요?"
}
```

**응답 예시:**
```
한국의 수도는 서울입니다.
```

## 프로젝트 구조

```
src/main/java/com/example/onkoreabe/
├── OnkoreaBeApplication.java          # 메인 애플리케이션
├── IndexController.java                # 홈 컨트롤러
├── auth/
│   └── CustomOAuth2UserService.java   # OAuth2 사용자 서비스
├── chat/
│   ├── ChatController.java            # 채팅 API 컨트롤러
│   ├── AiService.java                 # AI 서버 통신 서비스
│   └── dto/
│       ├── ChatRequest.java           # 채팅 요청 DTO
│       ├── AiApiRequest.java          # AI 요청 DTO
│       └── AiApiResponse.java         # AI 응답 DTO
├── config/
│   ├── SecurityConfig.java            # Spring Security 설정
│   └── WebClientConfig.java           # WebClient 설정
└── user/
    ├── User.java                       # 사용자 엔티티
    ├── Role.java                       # 권한 Enum
    └── UserRepository.java             # 사용자 리포지토리
```

## 보안 고려사항

### 현재 설정
- CSRF 보호: 비활성화 (API 서버용)
- Frame Options: 비활성화 (H2 콘솔용)

### 운영 환경 권장사항
1. 환경변수를 통한 민감 정보 관리
2. HTTPS 사용
3. CSRF 보호 활성화 (웹 애플리케이션의 경우)
4. H2 콘솔 비활성화
5. JPA ddl-auto를 `validate` 또는 `none`으로 설정
6. Flyway/Liquibase를 통한 DB 마이그레이션 관리

## 문제 해결

### MySQL 연결 오류
```
com.mysql.cj.jdbc.exceptions.CommunicationsException
```
- MySQL이 실행 중인지 확인
- 포트 번호 확인 (기본값: 3306)
- 방화벽 설정 확인

### OAuth2 인증 오류
```
invalid_client
```
- Google Cloud Console에서 Client ID/Secret 재확인
- 리디렉션 URI가 정확히 등록되었는지 확인
- 환경변수가 올바르게 설정되었는지 확인

### AI 서버 연결 오류
- Python AI 서버가 실행 중인지 확인
- `http://localhost:5000/generate-answer` 엔드포인트 확인
- Docker 환경에서는 `http://python-ai-service:5000` 사용

## 개발 상태

✅ 완료된 기능:
- Google OAuth2 로그인
- 사용자 관리
- AI 챗봇 API
- 에러 핸들링
- 입력 검증
- Swagger 문서화

⚠️ TODO:
- 단위 테스트 작성
- 통합 테스트 작성
- 로깅 개선
- 성능 최적화
- Python AI 서버 구현

## 라이선스

이 프로젝트는 교육 목적으로 작성되었습니다.
