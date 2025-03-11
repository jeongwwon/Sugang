package Hello.Sugang.domain.Dummy;

import Hello.Sugang.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DummyUserRepository extends JpaRepository<DummyUser,Long> {

    @Query("SELECT du FROM DummyUser du JOIN FETCH du.lecture WHERE du.student.id = :studentId")
    List<DummyUser> findByStudentIdWithLecture(@Param("studentId") Long studentId);

    List<DummyUser> findByStudentId(Long studentId);


    @Query("SELECT du FROM DummyUser du JOIN FETCH du.lecture WHERE du.id = :dummyUserId")
    Optional<DummyUser> findByIdWithLecture(@Param("dummyUserId") Long dummyUserId);

    List<DummyUser> findByLectureId(Long id);

}
