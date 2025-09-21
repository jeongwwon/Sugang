package Hello.Sugang.domain.enrollment.service;

import Hello.Sugang.domain.enrollmentLog.entity.EnrollmentLog;
import Hello.Sugang.domain.enrollmentLog.repository.EnrollmentLogRepository;
import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("db")
@RequiredArgsConstructor
public class DBEnrollmentService implements Enrollment {
    private final LectureRepository lectureRepository;
    private final EnrollmentLogRepository enrollmentLogRepository;

    @Override
    @Transactional
    public ResponseEntity<String> register(Long studentId, Long lectureId) {
        // 1. 강의를 비관적 락으로 조회 (좌석 차감 안전 보장)
        Lecture lecture = lectureRepository.findByIdForUpdate(lectureId);

        // 2. 잔여 좌석 확인
        if (lecture.getRemainingSeats() <= 0) {
            throw new IllegalStateException("해당 강의는 마감되었습니다: " + lecture.getName());
        }

        // 3. 수강 신청 로그 저장
        EnrollmentLog log = new EnrollmentLog(studentId, lecture);
        enrollmentLogRepository.save(log);

        // 4. 좌석 차감
        lecture.decreaseSeats();  // Lecture 엔티티 안에서 remainingSeats - 1
        return ResponseEntity.ok("성공");
    }
}
