package Hello.Sugang.domain.Dummy.service.enrollment;

import Hello.Sugang.domain.enrollmentLog.EnrollmentLog;
import Hello.Sugang.domain.enrollmentLog.EnrollmentLogRepository;
import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.student.Difficulty;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  lecture별 더미 유저들의 병렬 신청
 */
@Service
@RequiredArgsConstructor
public class DummyParallelEnrollmentService {

    private final ExecutorService executorService = Executors.newFixedThreadPool(50);
    private final DummyEnrollmentWorker dummyEnrollmentWorker;

    /**
     * 강의별 병렬 신청 처리
     */
    public void enrollLecture(Long lectureId, List<Long> dummyUserIds, Difficulty difficulty) {
        List<CompletableFuture<Void>> futures = dummyUserIds.stream()
                .map(dummyUserId -> CompletableFuture.runAsync(() -> {
                    try {
                        // 랜덤 지연 (100~500ms)
                        int delay = (difficulty != null) ? difficulty.getRandomDelay() : 300;
                        Thread.sleep(delay);
                        // 독립 트랜잭션 실행
                        dummyEnrollmentWorker.processOne(dummyUserId, lectureId);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }, executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}