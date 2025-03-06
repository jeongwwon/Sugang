package Hello.Sugang.domain.wishedlecture;

import Hello.Sugang.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishedLectureRepository extends JpaRepository<WishedLecture,Long> {
    List<WishedLecture> findByStudent(Student student);
    List<WishedLecture> findByStudentId(Long id);
    WishedLecture findByLectureId(Long id);
}
