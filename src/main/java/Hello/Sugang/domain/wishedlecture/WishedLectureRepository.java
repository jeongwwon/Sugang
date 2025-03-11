package Hello.Sugang.domain.wishedlecture;

import Hello.Sugang.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishedLectureRepository extends JpaRepository<WishedLecture,Long> {
    List<WishedLecture> findByStudent(Student student);
    @Query("SELECT w.lecture.id FROM WishedLecture w WHERE w.student.id = :studentId")
    List<Long> getWishedLectureIdsByStudentId(@Param("studentId") Long studentId);
    WishedLecture findByLectureId(Long id);
}
