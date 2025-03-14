package Hello.Sugang.domain.enrollment;

import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 *  수강 등록 클래스
 */
@Service
@RequiredArgsConstructor
public class EnrollmentProcessor {
    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public void processEnrollment(Long studentId, Long lectureId) {
        // 비관적 락을 통한 동기화
        Lecture targetLecture = lectureRepository.findByIdForUpdate(lectureId);

        if (targetLecture.getRemainingSeats() <= 0) {
            throw new IllegalStateException(targetLecture.getName() + " 강의는 마감되었습니다.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setLecture(targetLecture);
        enrollmentRepository.save(enrollment);
        targetLecture.decreaseSeats();
    }
}
