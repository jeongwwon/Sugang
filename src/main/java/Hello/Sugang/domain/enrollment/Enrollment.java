package Hello.Sugang.domain.enrollment;

import org.springframework.http.ResponseEntity;

import java.util.List;

public interface Enrollment {
    ResponseEntity<String> register(Long studentId, Long lectureId);
}
