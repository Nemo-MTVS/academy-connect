# 아카데미 커넥트(Academy Connect)

## 프로젝트 소개
아카데미 커넥트는 교육 기관에서 학생과 강사 간의 상담 예약, 점심 매칭 등을 관리하는 시스템입니다.

## 기술 스택
- **Backend**: Spring Boot
- **Database**: MySQL
- **ORM**: JPA/Hibernate
- **아키텍처**: DDD(Domain-Driven Design) 패턴

## 데이터베이스 구조

### 주요 엔티티
- **사용자(users)**: 시스템 사용자(학생, 강사)
- **반(class_groups)**: 사용자가 속한 반(예: 백엔드, 프론트엔드)
- **프로필(profiles)**: 사용자 프로필 정보
- **상담 슬롯(consulting_slots)**: 강사가 상담 가능한 시간대
- **상담 예약(consulting_bookings)**: 학생이 예약한 상담 정보
- **점심 매칭(lunch_matchings)**: 점심 식사 매칭 정보

### 데이터베이스 다이어그램
- **users**: 사용자 정보(ID, 로그인 정보, 이름, 역할 등)
- **class_groups**: 반 정보(이름, 생성일, 만료일)
- **profiles**: 사용자 프로필(깃허브, 블로그, 이메일, 프로필 파일 등)
- **consulting_slots**: 상담 가능 시간대(강사 ID, 시작/종료 시간, 상태)
- **consulting_bookings**: 상담 예약(학생 ID, 강사 ID, 상태, 메시지, 시간)
- **lunch_matching_classes**: 점심 매칭 분류
- **lunch_matchings**: 점심 매칭 정보

## 프로젝트 구조
프로젝트는 DDD(Domain-Driven Design) 패턴을 기반으로 구성되어 있습니다.

```
src/
├── main/
│   ├── java/
│   │   └── store/
│   │       └── mtvs/
│   │           └── academyconnect/
│   │               ├── user/                 # 사용자 도메인
│   │               │   ├── controller/       # 사용자 관련 API 컨트롤러
│   │               │   ├── application/      # 사용자 서비스 계층
│   │               │   ├── domain/           # 사용자 도메인 모델
│   │               │   │   ├── entity/       # 사용자 엔티티
│   │               │   │   ├── vo/           # 사용자 값 객체
│   │               │   │   └── service/      # 사용자 도메인 서비스
│   │               │   └── infrastructure/   # 사용자 인프라 계층
│   │               │       └── repository/   # 사용자 레포지토리 구현
│   │               │
│   │               ├── profile/              # 프로필 도메인
│   │               │   ├── controller/       # 프로필 관련 API 컨트롤러
│   │               │   ├── application/      # 프로필 서비스 계층
│   │               │   ├── domain/           # 프로필 도메인 모델
│   │               │   └── infrastructure/   # 프로필 인프라 계층
│   │               │
│   │               ├── lunchmatching/        # 점심 매칭 도메인
│   │               │   ├── controller/       # 점심 매칭 API 컨트롤러
│   │               │   ├── application/      # 점심 매칭 서비스 계층
│   │               │   ├── domain/           # 점심 매칭 도메인 모델
│   │               │   └── infrastructure/   # 점심 매칭 인프라 계층
│   │               │
│   │               ├── global/               # 공통 기능
│   │               │   ├── config/           # 애플리케이션 설정
│   │               │   ├── error/            # 예외 처리
│   │               │   ├── util/             # 유틸리티 클래스
│   │               │   └── security/         # 보안 설정
│   │               │
│   │               ├── consulting/           # 상담 도메인
│   │               │   ├── controller/       # 상담 관련 API 컨트롤러
│   │               │   ├── application/      # 상담 서비스 계층
│   │               │   ├── domain/           # 상담 도메인 모델
│   │               │   └── infrastructure/   # 상담 인프라 계층
│   │               │
│   │               ├── classgroup/           # 반 도메인
│   │               │   ├── controller/       # 반 관련 API 컨트롤러
│   │               │   ├── application/      # 반 서비스 계층
│   │               │   ├── domain/           # 반 도메인 모델
│   │               │   └── infrastructure/   # 반 인프라 계층
│   │               │
│   │               └── AcademyConnectApplication.java  # 메인 애플리케이션
│   └── resources/
│       ├── application.yml   # 애플리케이션 설정
│       ├── static/           # 정적 파일
│       └── templates/        # 템플릿 파일
└── test/                     # 테스트 코드
    └── java/
        └── store/
            └── mtvs/
                └── academyconnect/
                    ├── user/            # 사용자 도메인 테스트
                    ├── profile/         # 프로필 도메인 테스트
                    ├── lunchmatching/   # 점심 매칭 도메인 테스트
                    ├── consulting/      # 상담 도메인 테스트
                    └── classgroup/      # 반 도메인 테스트
```

### DDD 계층 구조 설명
- **Controller**: API 엔드포인트를 제공하고 요청을 처리하는 계층
- **Application**: 비즈니스 로직을 처리하는 서비스 계층
- **Domain**: 핵심 비즈니스 로직과 규칙을 포함하는 도메인 모델 계층
  - **Entity**: 고유한 식별자를 가진 도메인 객체
  - **Value Object**: 값으로 식별되는 불변 객체
  - **Domain Service**: 여러 엔티티에 걸친 도메인 로직
- **Infrastructure**: 외부 시스템과의 통신, 데이터베이스 접근을 담당하는 계층
  - **Repository**: 도메인 객체의 저장 및 조회 인터페이스와 구현

## 설치 및 실행 방법
1. 저장소 클론
   ```bash
   git clone [repository-url]
   ```

2. 데이터베이스 초기화
   ```bash
   mysql -u [username] -p < init.sql
   ```

3. 애플리케이션 설정 (src/main/resources/application.yml)
   - 데이터베이스 연결 정보 설정

4. 애플리케이션 실행
   ```bash
   ./gradlew bootRun
   ```

## 시스템 기능
- 사용자 관리(학생, 강사)
- 반 관리
- 프로필 관리
- 상담 예약 관리
- 점심 매칭 관리 