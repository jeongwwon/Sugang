USE PORTFOLIO;
SET SESSION cte_max_recursion_depth = 1000000;
INSERT INTO student (email, password, name, difficulty)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n <= 5000
)
SELECT
    CONCAT('student', n, '@email.com'),
    CONCAT('pw', n),
    CONCAT('Student', n),
    CASE n % 3
        WHEN 0 THEN 'EASY'
        WHEN 1 THEN 'MEDIUM'
        ELSE 'HARD'
    END
FROM numbers;


INSERT INTO lecture (school, department, name, lecture_time, remaining_seats, total_seats)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n <= 1000
)
SELECT
    CONCAT('School', n % 10),
    CONCAT('Department', n % 50),
    CONCAT('Lecture', n),
    DATE_ADD('2024-01-01', INTERVAL n DAY),
    100,
    100
FROM numbers;


INSERT INTO enrollment_log (student_id, lecture_id, status)
WITH RECURSIVE numbers AS (
    SELECT 1 AS n
    UNION ALL
    SELECT n + 1 FROM numbers WHERE n < 1000000
)
SELECT
    ((n - 1) % 5000) + 1 AS student_id,                -- 학생 1~5000 균등 분배
    (((n - 1) / 5000) % 1000) + 1 AS lecture_id,       -- 강의 1~1000 균등 분배, 학생마다 unique
    'SUCCESS'
FROM numbers;




