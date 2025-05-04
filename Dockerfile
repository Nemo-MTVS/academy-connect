# 실행 환경용 OpenJDK 이미지 사용
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 로컬에서 빌드된 JAR 파일 복사
COPY build/libs/*-SNAPSHOT.jar app.jar

# Spring Boot 기본 포트
EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
