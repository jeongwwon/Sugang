package Hello.Sugang.domain.enrollment.Facade;

import Hello.Sugang.domain.enrollment.service.Enrollment;
import Hello.Sugang.domain.enrollment.service.FindEnrollment;
import Hello.Sugang.domain.enrollmentLog.repository.EnrollmentLogRepository;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import Hello.Sugang.domain.wishedlecture.service.WishedLectureService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EnrollmentFacade {

    private final Map<String, Enrollment> enrollmentMap;
    private final WishedLectureService wishedLectureService;
    private final LectureRepository lectureRepository;
    private final EnrollmentLogRepository enrollmentLogRepository;

    public ResponseEntity<String> enroll(String mode, Long studentId, Long lectureId) {
        return enrollmentMap.get(mode).register(studentId,lectureId);
    }

    @Transactional
    public void initialize(Long studentId) {
        List<Long> lectureIds = wishedLectureService.getWishedLecturesIdByStudentId(studentId);

        if (!lectureIds.isEmpty()) {
            lectureRepository.bulkInitializeLectures(lectureIds);
            enrollmentLogRepository.bulkDeleteByLectureIds(lectureIds);
        }
    }
}
