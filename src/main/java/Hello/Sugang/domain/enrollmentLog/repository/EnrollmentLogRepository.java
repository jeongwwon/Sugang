package Hello.Sugang.domain.enrollmentLog.repository;

import Hello.Sugang.domain.collegeStats.entity.CollegeStats;
import Hello.Sugang.domain.enrollmentLog.entity.EnrollmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentLogRepository extends JpaRepository<EnrollmentLog, Long> {

    List<EnrollmentLog> findByStudentId(Long studentId);

    @Query("SELECT e FROM EnrollmentLog e JOIN FETCH e.lecture WHERE e.studentId = :studentId")
    List<EnrollmentLog> findWithLectureByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT e FROM EnrollmentLog e " +
            "JOIN FETCH e.lecture l " +
            "WHERE l.school = :school AND l.department = :department")
    List<EnrollmentLog> findBySchoolAndDepartment(@Param("school") String school,
                                                  @Param("department") String department);
    @Query("SELECT c FROM CollegeStats c " +
            "WHERE c.school = :school AND c.department = :department")
    List<CollegeStats> findBySchoolAndDepartment2(@Param("school") String school,
                                                 @Param("department") String department);

    // 벌크 DELETE 적용 (동시성 문제 방지됨)
    @Modifying
    @Query("DELETE FROM EnrollmentLog e WHERE e.lecture.id IN :lectureIds")
    void bulkDeleteByLectureIds(@Param("lectureIds") List<Long> lectureIds);

}

