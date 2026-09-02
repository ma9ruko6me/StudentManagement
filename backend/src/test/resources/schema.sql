CREATE TABLE IF NOT EXISTS students
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    furigana VARCHAR(50),
    nickname VARCHAR(50),
    age INT,
    email VARCHAR(50),
    area VARCHAR(50),
    gender VARCHAR(50),
    remark VARCHAR(300),
    is_deleted BOOLEAN
);

CREATE TABLE IF NOT EXISTS students_courses
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    student_id BIGINT NOT NULL,
    course_name VARCHAR(50),
    course_start_at TIMESTAMP,
    course_end_at TIMESTAMP
);

CREATE TABLE IF NOT EXISTS course_applications
(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL UNIQUE,
    application_status VARCHAR(20) NOT NULL
);