package Hello.Sugang.domain.enrollment.service;

import Hello.Sugang.domain.enrollment.Enrollment;
import Hello.Sugang.domain.enrollment.EnrollmentRepository;
import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class SingleEnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final LectureRepository lectureRepository;
    /**
     *  HTTP 요청을 통한 최종 등록
     */
    @Transactional
    public void enrollDummyUser(Long dummyUserId,Long lectureId) {
        Lecture targetLecture = lectureRepository.findByIdForUpdate(lectureId);

        if (targetLecture.getRemainingSeats() <= 0) {
            throw new IllegalStateException(targetLecture.getName() + " 강의는 마감되었습니다.");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(dummyUserId);
        enrollment.setLecture(targetLecture);
        enrollmentRepository.save(enrollment);
        targetLecture.decreaseSeats();
    }
}
