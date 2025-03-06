package Hello.Sugang.domain.Dummy;

import Hello.Sugang.domain.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DummyUserRepository extends JpaRepository<DummyUser,Long> {
    public List<DummyUser> findByStudentId(Long studentId);
}
