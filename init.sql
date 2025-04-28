DROP TABLE IF EXISTS lunch_matchings;
DROP TABLE IF EXISTS lunch_matching_classes;

DROP TABLE IF EXISTS undefined_consulting_bookings;
DROP TABLE IF EXISTS undefined_consultings;

DROP TABLE IF EXISTS consulting_bookings;
DROP TABLE IF EXISTS consulting_slots;

DROP TABLE IF EXISTS profiles;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS class_groups;

CREATE TABLE `consulting_bookings` (
                                      `id` BIGINT NOT NULL,
                                      `student_id` VARCHAR(255) NOT NULL COMMENT 'UUID',
                                      `instructor_id` VARCHAR(255) NOT NULL COMMENT 'UUID',
                                      `status` ENUM('취소됨','상담완료','예약됨') NOT NULL DEFAULT '예약됨' COMMENT '예약 상태',
                                      `message` VARCHAR(255) NULL COMMENT '~~ 점이 궁금해요',
                                      `created_at` DATETIME NOT NULL,
                                      `update_at` DATETIME NOT NULL,
                                      `start_time` DATETIME NOT NULL,
                                      `end_time` DATETIME NOT NULL
);

CREATE TABLE `users` (
                        `id` VARCHAR(255) NOT NULL COMMENT 'UUID',
                        `class_group_id` BIGINT NOT NULL,
                        `login_id` VARCHAR(255) NOT NULL COMMENT '암호화(복호화 가능)',
                        `passwd` VARCHAR(255) NOT NULL COMMENT '암호화',
                        `name` VARCHAR(255) NOT NULL COMMENT '암호화(복호화 가능)',
                        `created_at` DATETIME NOT NULL,
                        `updated_at` DATETIME NOT NULL,
                        `deleted_at` DATETIME NULL,
                        `role` VARCHAR(255) NOT NULL
);

CREATE TABLE `class_groups` (
                              `id` BIGINT NOT NULL,
                              `name` VARCHAR(255) NOT NULL COMMENT '백엔드',
                              `created_at` DATETIME NOT NULL,
                              `expired_at` DATETIME NOT NULL
);

CREATE TABLE `consulting_slots` (
                                   `id` BIGINT NOT NULL,
                                   `instructor_id` VARCHAR(255) NOT NULL COMMENT 'UUID',
                                   `start_time` DATETIME NOT NULL,
                                   `end_time` DATETIME NOT NULL,
                                   `created_at` DATETIME NOT NULL COMMENT '등록시간 기준',
                                   `deleted_at` DATETIME NULL COMMENT '제거일',
                                   `status` ENUM('사용가능','불가능') NOT NULL
);

CREATE TABLE `undefined_consultings` (
                                        `id` BIGINT NOT NULL,
                                        `student_id` VARCHAR(255) NOT NULL COMMENT 'UUID',
                                        `instructor_id` VARCHAR(255) NOT NULL COMMENT 'UUID',
                                        `request_at` DATETIME NOT NULL,
                                        `status` ENUM('WAITING', 'DONE') NOT NULL,
                                        `updated_at` DATETIME NOT NULL,
                                        `comment` TEXT NULL COMMENT '상담 요청 내용'

);

CREATE TABLE `undefined_consulting_bookings` (
                                                 `id` BIGINT NOT NULL,
                                                 `consulting_booking_id` BIGINT NOT NULL,
                                                 `undefined_consulting_id` BIGINT NOT NULL,
                                                 `created_at` DATETIME NOT NULL
);

CREATE TABLE `profiles` (
                           `id` VARCHAR(255) NOT NULL COMMENT 'UUID',
                           `md` LONGTEXT NULL,
                           `github` VARCHAR(255) NULL,
                           `blog` VARCHAR(255) NULL,
                           `email` VARCHAR(255) NULL,
                           `file_path` VARCHAR(255) NULL
);

CREATE TABLE `lunch_matching_classes` (
                                        `id` BIGINT NOT NULL,
                                        `created_at` VARCHAR(255) NULL,
                                        `name` VARCHAR(255) NULL COMMENT 'BE, TA'
);

CREATE TABLE `lunch_matchings` (
                                  `id` VARCHAR(255) NOT NULL,
                                  `lunch_matching_id` BIGINT NOT NULL,
                                  `user_id` VARCHAR(255) NOT NULL COMMENT 'UUID',
                                  `created_at` DATETIME NOT NULL,
                                  `deleted_at` DATETIME NULL
);

-- ✅ PRIMARY KEY
ALTER TABLE class_groups ADD CONSTRAINT PK_class_groups PRIMARY KEY (id);
ALTER TABLE users ADD CONSTRAINT PK_users PRIMARY KEY (id);
ALTER TABLE profiles ADD CONSTRAINT PK_profiles PRIMARY KEY (id);
ALTER TABLE consulting_slots ADD CONSTRAINT PK_consulting_slots PRIMARY KEY (id);
ALTER TABLE consulting_bookings ADD CONSTRAINT PK_consulting_booking PRIMARY KEY (id);
ALTER TABLE undefined_consultings ADD CONSTRAINT PK_undefined_consulting PRIMARY KEY (id);
ALTER TABLE undefined_consulting_bookings ADD CONSTRAINT PK_undefined_consulting_bookling PRIMARY KEY (id);
ALTER TABLE lunch_matching_classes ADD CONSTRAINT PK_lunch_matching_class PRIMARY KEY (id);
ALTER TABLE lunch_matchings ADD CONSTRAINT PK_lunch_matching PRIMARY KEY (id);

-- ✅ FOREIGN KEY
-- users.class_group_id → class_groups.id
ALTER TABLE users
    ADD CONSTRAINT FK_users_classGroup
        FOREIGN KEY (class_group_id) REFERENCES class_groups(id);

-- profiles.id → users.id
ALTER TABLE profiles
    ADD CONSTRAINT FK_profiles_user
        FOREIGN KEY (id) REFERENCES users(id);

-- consulting_slots.instructor_id → users.id
ALTER TABLE consulting_slots
    ADD CONSTRAINT FK_consulting_slots_instructor
        FOREIGN KEY (instructor_id) REFERENCES users(id);

-- consulting_bookings.student_id → users.id
ALTER TABLE consulting_bookings
    ADD CONSTRAINT FK_consulting_booking_student
        FOREIGN KEY (student_id) REFERENCES users(id);

-- consulting_bookings.instructor_id → users.id
ALTER TABLE consulting_bookings
    ADD CONSTRAINT FK_consulting_booking_instructor
        FOREIGN KEY (instructor_id) REFERENCES users(id);

-- undefined_consultings.student_id → users.id
ALTER TABLE undefined_consultings
    ADD CONSTRAINT FK_undefined_consulting_student
        FOREIGN KEY (student_id) REFERENCES users(id);

-- undefined_consultings.id2instructor_id → users.id
ALTER TABLE undefined_consultings
    ADD CONSTRAINT FK_undefined_consulting_instructor
        FOREIGN KEY (instructor_id) REFERENCES users(id);

-- undefined_consulting_bookling.consulting_booking_id → consulting_bookings.id
ALTER TABLE undefined_consulting_bookings
    ADD CONSTRAINT FK_undefined_consulting_bookling_booking
        FOREIGN KEY (consulting_booking_id) REFERENCES consulting_bookings(id);

-- undefined_consulting_bookling.undefined_consulting_id → undefined_consultings.id
ALTER TABLE undefined_consulting_bookings
    ADD CONSTRAINT FK_undefined_consulting_bookling_request
        FOREIGN KEY (undefined_consulting_id) REFERENCES undefined_consultings(id);

-- lunch_matching.lunch_matching_id → lunch_matching_class.id
ALTER TABLE lunch_matchings
    ADD CONSTRAINT FK_lunch_matching_class
        FOREIGN KEY (lunch_matching_id) REFERENCES lunch_matching_classes(id);

-- lunch_matching.user_id → users.id
ALTER TABLE lunch_matchings
    ADD CONSTRAINT FK_lunch_matching_user
        FOREIGN KEY (user_id) REFERENCES users(id);

