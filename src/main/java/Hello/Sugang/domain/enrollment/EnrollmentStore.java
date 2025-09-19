package Hello.Sugang.domain.enrollment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EnrollmentStore {

    private final Map<String, Enrollment> enrollmentMap;

    public Enrollment findEnrollment(String mode) {
        Enrollment enrollment = enrollmentMap.get(mode.toLowerCase());
        if (enrollment == null) {
            throw new IllegalArgumentException("Invalid mode: " + mode);
        }
        return enrollment;
    }
}

