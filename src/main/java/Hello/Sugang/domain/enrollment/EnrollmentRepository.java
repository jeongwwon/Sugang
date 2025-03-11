package Hello.Sugang.domain.enrollment;

import Hello.Sugang.domain.lecture.Lecture;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);
    List<Enrollment> findByLectureId(Long lectureId);
    void deleteByLectureId(Long LectureId);
    boolean existsByStudentIdAndLectureId(Long studentId, Long lectureId);

    // 벌크 DELETE 적용 (동시성 문제 방지됨)
    @Modifying
    @Query("DELETE FROM Enrollment e WHERE e.lecture.id IN :lectureIds")
    void bulkDeleteByLectureIds(@Param("lectureIds") List<Long> lectureIds);

}

