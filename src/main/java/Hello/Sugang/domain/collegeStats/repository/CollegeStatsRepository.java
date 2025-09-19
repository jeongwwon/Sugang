package Hello.Sugang.domain.collegeStats.repository;

import Hello.Sugang.domain.collegeStats.entity.CollegeStats;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollegeStatsRepository extends JpaRepository<CollegeStats, Long> {

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO college_stats (school, department, student_count, success_count, fail_count) " +
            "SELECT l.school, l.department, " +
            "       COUNT(el.id) AS student_count, " +
            "       SUM(CASE WHEN el.status = 'SUCCESS' THEN 1 ELSE 0 END) AS success_count, " +
            "       SUM(CASE WHEN el.status = 'FAIL' THEN 1 ELSE 0 END) AS fail_count " +
            "FROM enrollment_log el " +
            "JOIN lecture l ON el.lecture_id = l.lecture_id " +
            "WHERE l.school = :school AND l.department = :department " +
            "GROUP BY l.school, l.department",
            nativeQuery = true)
    int insertCollegeStats(@Param("school") String school,
                           @Param("department") String department);
}
