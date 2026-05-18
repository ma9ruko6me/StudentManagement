CREATE TABLE IF NOT EXISTS students
(
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50),
    hurigana VARCHAR(50),
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
    course VARCHAR(50),
    start_date TIMESTAMP,
    end_date TIMESTAMP
);