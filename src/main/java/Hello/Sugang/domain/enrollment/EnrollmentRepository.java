package Hello.Sugang.domain.enrollment;

import Hello.Sugang.domain.lecture.Lecture;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);
    void deleteByStudentId(Long studentId);
    boolean existsByStudentIdAndLectureId(Long studentId, Long lectureId);
}

