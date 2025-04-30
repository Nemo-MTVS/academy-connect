INSERT INTO class_groups (id, name, created_at, expired_at)
VALUES
    (1, 'Backend', NOW(), DATE_ADD(NOW(), INTERVAL 8 MONTH)),
    (2, 'Unity', NOW(), DATE_ADD(NOW(), INTERVAL 8 MONTH)),
    (3, 'TA', NOW(), DATE_ADD(NOW(), INTERVAL 8 MONTH));
UPDATE users SET role = 'INSTRUCTOR' WHERE id = '1145d1f5-7fb2-43d1-8777-00b9fbe2b43a'

INSERT INTO users (id, class_group_id, login_id, passwd, name, created_at, updated_at, deleted_at, role)
VALUES
-- BackEnd 반 (class_group_id = 1)
('uuid-be-001', 1, 'login_be_001', 'pass_be_001', '홍길동1', NOW(), NOW(),NULL, 'STUDENT'),
('uuid-be-002', 1, 'login_be_002', 'pass_be_002', '홍길동2', NOW(), NOW(),NULL, 'STUDENT'),
('uuid-be-003', 1, 'login_be_003', 'pass_be_003', '홍길동3', NOW(), NOW(),NULL, 'STUDENT'),

-- Unity 반 (class_group_id = 2)
('uuid-unity-001', 2, 'login_unity_001', 'pass_unity_001', '임꺽정1', NOW(), NOW(),NULL, 'STUDENT'),
('uuid-unity-002', 2, 'login_unity_002', 'pass_unity_002', '임꺽정2', NOW(), NOW(),NULL, 'STUDENT'),
('uuid-unity-003', 2, 'login_unity_003', 'pass_unity_003', '임꺽정3', NOW(), NOW(),NULL, 'STUDENT'),

-- TA 반 (class_group_id = 3)
('uuid-ta-001', 3, 'login_ta_001', 'pass_ta_001', '성춘향1', NOW(), NOW(),NULL, 'STUDENT'),
('uuid-ta-002', 3, 'login_ta_002', 'pass_ta_002', '성춘향2', NOW(), NOW(),NULL, 'STUDENT'),
('uuid-ta-003', 3, 'login_ta_003', 'pass_ta_003', '성춘향3', NOW(), NOW(),NULL, 'STUDENT');

INSERT INTO users (id, class_group_id, login_id, passwd, name, created_at, updated_at, deleted_at, role)
VALUES
-- BackEnd 반 강사
# ('uuid-be-ins', 1, 'login_be_ins', 'pass_be_ins', '백엔드강사', NOW(), NOW(),NULL, 'INSTRUCTOR'),
('uuid-be-ins', 1, 'login_be_ins', 'pass_be_ins', '백엔드강사', NOW(), NOW(),NULL, 'TEACHER'),

-- Unity 반 강사
('uuid-unity-ins', 2, 'login_unity_ins', 'pass_unity_ins', '유니티강사', NOW(), NOW(),NULL, 'TEACHER'),

-- TA 반 강사
('uuid-ta-ins', 3, 'login_ta_ins', 'pass_ta_ins', 'TA강사', NOW(), NOW(),NULL, 'TEACHER');



INSERT INTO profiles (id, md, github, blog, email, file_path)
VALUES
-- BackEnd 학생들
('uuid-be-001', '# 안녕하세요! 👋\n백엔드 개발자를 꿈꾸는 학생입니다.\n- 관심 기술: Spring Boot, MySQL\n- 목표: 서비스 개발 경험 쌓기', 'https://github.com/be-student1', 'https://blog.com/be-student1', 'be1@example.com', NULL),
('uuid-be-002', '# 반갑습니다!\nSpring과 JPA를 배우고 있어요.\n- 관심사: 서버 아키텍처, REST API', 'https://github.com/be-student2', 'https://blog.com/be-student2', 'be2@example.com', NULL),
('uuid-be-003', '# 백엔드 개발 공부 중입니다 🧑‍💻\n- 좋아하는 것: DB 설계, 쿼리 최적화', 'https://github.com/be-student3', 'https://blog.com/be-student3', 'be3@example.com', NULL),

-- Unity 학생들
('uuid-unity-001', '# Unity 개발에 관심이 많아요 🎮\n- 배우고 싶은 것: 3D 물리 엔진, 캐릭터 컨트롤', 'https://github.com/unity-student1', 'https://blog.com/unity-student1', 'unity1@example.com', NULL),
('uuid-unity-002', '# 게임 만드는 게 즐거운 학생입니다!\n- 목표: Unity로 첫 게임 출시하기', 'https://github.com/unity-student2', 'https://blog.com/unity-student2', 'unity2@example.com', NULL),
('uuid-unity-003', '# 2D 게임 제작 중입니다 🎨\n- 좋아하는 장르: 퍼즐, 인디게임', 'https://github.com/unity-student3', 'https://blog.com/unity-student3', 'unity3@example.com', NULL),

-- TA 학생들
('uuid-ta-001', '# TA 트랙 학생입니다\n- 관심 주제: 프로젝트 관리, 코드 리뷰', 'https://github.com/ta-student1', 'https://blog.com/ta-student1', 'ta1@example.com', NULL),
('uuid-ta-002', '# 안녕하세요!\n- 공부하고 있는 것: 팀 협업, Git 활용법', 'https://github.com/ta-student2', 'https://blog.com/ta-student2', 'ta2@example.com', NULL),
('uuid-ta-003', '# 열심히 배우는 중입니다 💪\n- 역할: 팀 내 일정 정리 담당', 'https://github.com/ta-student3', 'https://blog.com/ta-student3', 'ta3@example.com', NULL),
-- BackEnd 강사
('uuid-be-ins', '스스로의 가능성에 도전해보세요.여러분의 성장과 도전을 응원하겠습니다.', 'https://github.com/be-instructor', 'https://blog.com/be-instructor', 'be-ins@example.com', NULL),

-- Unity 강사
('uuid-unity-ins', '메타버스 콘텐츠 개발자 양성을 위해 최선을 다 하겠습니다.', 'https://github.com/unity-instructor', 'https://blog.com/unity-instructor', 'unity-ins@example.com', NULL),

-- TA 강사
('uuid-ta-ins', '3D모델링과 3D스캐닝 기술을 활용하여 메타버스 플렛폼 및 적용 가능한 소스를 제작하는데 도움이 되고 싶습니다.', 'https://github.com/ta-instructor', 'https://blog.com/ta-instructor', 'ta-ins@example.com', NULL);
