package Hello.Sugang.domain.Dummy.service.enrollment;

import Hello.Sugang.domain.enrollmentLog.entity.EnrollmentLog;
import Hello.Sugang.domain.enrollmentLog.repository.EnrollmentLogRepository;
import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DummyEnrollmentWorker {
    private final LectureRepository lectureRepository;
    private final EnrollmentLogRepository enrollmentLogRepository;

    @Transactional
    public void processOne(Long dummyUserId, Long lectureId) {

        // 비관적 락으로 좌석 확보
        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId);
        if (lecture.getRemainingSeats() <= 0) {
            throw new IllegalStateException(lecture.getName() + " 강의는 마감되었습니다.");
        }

        EnrollmentLog enrollment = new EnrollmentLog(dummyUserId, lecture);
        enrollmentLogRepository.save(enrollment);

        lecture.decreaseSeats(); // 좌석 차감
    }
}
