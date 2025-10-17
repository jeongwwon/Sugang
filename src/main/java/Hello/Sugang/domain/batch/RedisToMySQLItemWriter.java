package Hello.Sugang.domain.batch;

import Hello.Sugang.domain.enrollmentLog.entity.EnrollmentLog;
import Hello.Sugang.domain.enrollmentLog.entity.Status;
import Hello.Sugang.domain.enrollmentLog.repository.EnrollmentLogRepository;
import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisToMySQLItemWriter implements ItemWriter<RedisLectureDTO> {

    private final LectureRepository lectureRepository;
    private final EnrollmentLogRepository enrollmentLogRepository;

    @Transactional
    @Override
    public void write(Chunk<? extends RedisLectureDTO> items) {
        for (RedisLectureDTO dto : items) {
            Lecture lecture = lectureRepository.findById(dto.getLectureId())
                    .orElseThrow(() -> new IllegalArgumentException("Lecture not found: " + dto.getLectureId()));

            // 좌석 수 동기화
            int remaining = lecture.getRemainingSeats() - dto.getCount();
            lecture.setRemainingSeats(Math.max(remaining, 0));
            lectureRepository.save(lecture);

            // 신청자 로그 저장
            for (Long studentId : dto.getEnrolledStudents()) {
                EnrollmentLog log = new EnrollmentLog(studentId, lecture);
                log.setStatus(Status.SUCCESS);
                enrollmentLogRepository.save(log);
            }

            System.out.printf("Lecture %d: %d명 처리 완료, 잔여 %d명%n",
                    dto.getLectureId(), dto.getCount(), lecture.getRemainingSeats());
        }
    }
}
