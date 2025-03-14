package Hello.Sugang.domain.Dummy;

import Hello.Sugang.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DummyUserRepository extends JpaRepository<DummyUser,Long> {

    List<DummyUser> findByStudentId(Long studentId);

    List<DummyUser> findByLectureId(Long id);

    @Query("SELECT du.id FROM DummyUser du WHERE du.lecture.id = :lectureId")
    List<Long> findIdsByLectureId(@Param("lectureId") Long lectureId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("DELETE FROM DummyUser du WHERE du.id IN :dummyUserIds")
    void bulkDelete(@Param("dummyUserIds") List<Long> dummyUserIds);
}
