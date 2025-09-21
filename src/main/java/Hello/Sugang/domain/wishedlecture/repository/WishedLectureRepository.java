package Hello.Sugang.domain.wishedlecture.repository;

import Hello.Sugang.domain.student.entity.Student;
import Hello.Sugang.domain.wishedlecture.entity.WishedLecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WishedLectureRepository extends JpaRepository<WishedLecture,Long> {
    List<WishedLecture> findByStudent(Student student);

    WishedLecture findByLectureId(Long id);

    @Query("SELECT w.lecture.id FROM WishedLecture w WHERE w.student.id = :studentId")
    List<Long> getWishedLectureIdsByStudentId(@Param("studentId") Long studentId);
}
