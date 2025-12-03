#!/bin/bash
set -e

# EC2 배포 스크립트
# 사용법: ./deploy-ec2.sh

echo "🚀 EC2 배포 시작..."

# 1. .env 파일 로드
if [ -f .env ]; then
    echo "📄 .env 파일 로드 중..."
    set -a
    source .env
    set +a
    echo "✅ 환경변수 로드 완료"
else
    echo "⚠️  .env 파일을 찾을 수 없습니다"
fi

# 2. 환경변수 확인
if [ -z "$DB_HOST" ]; then
    echo "❌ 에러: DB_HOST 환경변수가 설정되지 않았습니다"
    echo ""
    echo ".env 파일을 생성하거나 환경변수를 직접 설정해주세요:"
    echo "  cp .env.example .env"
    echo "  vi .env"
    echo ""
    echo "또는 직접 설정:"
    echo "  export DB_HOST=your-rds-endpoint.amazonaws.com"
    echo "  export DB_USERNAME=admin"
    echo "  export DB_PASSWORD=your-password"
    echo "  export JWT_SECRET=your-secret-key"
    echo "  export GOOGLE_CLIENT_ID=your-client-id"
    echo "  export GOOGLE_CLIENT_SECRET=your-client-secret"
    exit 1
fi

# 3. Git Pull
echo "📥 최신 코드 가져오기..."
git pull origin develop

# 4. Profile 설정
export SPRING_PROFILES_ACTIVE=prod
echo "✅ Spring Profile: $SPRING_PROFILES_ACTIVE"

# 5. 기존 프로세스 종료
echo "🛑 기존 프로세스 확인 중..."
if [ -f app.pid ]; then
    OLD_PID=$(cat app.pid)
    if ps -p $OLD_PID > /dev/null 2>&1; then
        echo "🛑 기존 프로세스 종료 중... (PID: $OLD_PID)"
        kill -15 $OLD_PID
        sleep 3

        # 강제 종료가 필요한 경우
        if ps -p $OLD_PID > /dev/null 2>&1; then
            echo "⚠️  강제 종료 중..."
            kill -9 $OLD_PID
        fi
        echo "✅ 기존 프로세스 종료 완료"
    else
        echo "ℹ️  실행 중인 프로세스 없음"
    fi
    rm -f app.pid
else
    echo "ℹ️  PID 파일 없음 (최초 배포 또는 수동 종료됨)"
fi

# 6. 로그 디렉토리 생성
mkdir -p logs

# 7. 빌드
echo "🔨 빌드 중..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패"
    exit 1
fi

echo "✅ 빌드 완료"

# 8. JAR 파일 확인
JAR_FILE=$(ls build/libs/*.jar 2>/dev/null | grep -v plain | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "❌ JAR 파일을 찾을 수 없습니다"
    exit 1
fi

echo "✅ JAR 파일: $JAR_FILE"

# 9. 애플리케이션 백그라운드 실행
echo "🚀 애플리케이션 실행 중..."
nohup java -jar \
    -Dspring.profiles.active=prod \
    -Dserver.port=8080 \
    "$JAR_FILE" \
    > ./logs/application.log 2>&1 &

NEW_PID=$!
echo $NEW_PID > app.pid

echo "✅ 배포 완료!"
echo "📌 PID: $NEW_PID"
echo "📝 로그 확인: tail -f ./logs/application.log"
echo "🏥 Health Check: curl http://localhost:8080/actuator/health"

# 10. 헬스체크 대기
echo ""
echo "⏳ 애플리케이션 시작 대기 중..."
sleep 5

for i in {1..10}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ 애플리케이션 정상 시작!"
        curl http://localhost:8080/actuator/health
        exit 0
    fi
    echo "⏳ 대기 중... ($i/10)"
    sleep 3
done

echo "⚠️  타임아웃: 애플리케이션 시작 확인 실패"
echo "📝 로그를 확인하세요: tail -f ./logs/application.log"
exit 1
