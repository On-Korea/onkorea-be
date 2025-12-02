#!/bin/bash

# EC2 배포 스크립트
# 사용법: ./deploy-ec2.sh

echo "🚀 EC2 배포 시작..."

# 1. 환경변수 확인
if [ -z "$DB_HOST" ]; then
    echo "❌ 에러: DB_HOST 환경변수가 설정되지 않았습니다"
    echo "다음 환경변수를 설정해주세요:"
    echo "  export DB_HOST=your-rds-endpoint.amazonaws.com"
    echo "  export DB_USERNAME=admin"
    echo "  export DB_PASSWORD=your-password"
    echo "  export JWT_SECRET=your-secret-key"
    echo "  export GOOGLE_CLIENT_ID=your-client-id"
    echo "  export GOOGLE_CLIENT_SECRET=your-client-secret"
    exit 1
fi

# 2. Profile 설정
export SPRING_PROFILES_ACTIVE=prod
echo "✅ Spring Profile: $SPRING_PROFILES_ACTIVE"

# 3. 빌드
echo "🔨 빌드 중..."
./gradlew clean build -x test

if [ $? -ne 0 ]; then
    echo "❌ 빌드 실패"
    exit 1
fi

echo "✅ 빌드 완료"

# 4. JAR 파일 확인
JAR_FILE=$(ls build/libs/*.jar 2>/dev/null | head -1)

if [ -z "$JAR_FILE" ]; then
    echo "❌ JAR 파일을 찾을 수 없습니다"
    exit 1
fi

echo "✅ JAR 파일: $JAR_FILE"

# 5. 실행
echo "🚀 애플리케이션 실행 중..."
java -jar "$JAR_FILE"
