package Hello.Sugang.domain.enrollment;


import Hello.Sugang.domain.Dummy.DummyUser;
import Hello.Sugang.domain.Dummy.DummyUserRepository;
import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.wishedlecture.WishedLectureService;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import Hello.Sugang.domain.wishedlecture.WishedLecture;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import Hello.Sugang.domain.enrollment.network.HttpEnrollment;
@Service
@Slf4j
@Timed("my.enrollment")
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final DummyUserRepository dummyUserRepository;
    private final WishedLectureService wishedLectureService;
    private final LectureRepository lectureRepository;
    private final EnrollmentProcessor enrollmentProcessor;
    private final ExecutorService executorService = Executors.newFixedThreadPool(50); // 병렬 HTTP 요청을 위한 스레드 풀

    /**
     *  학생의 신청 여부 확인
     */
    public Map<Long, Boolean> findRegistryList(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream().collect(Collectors.toMap(e -> e.getLecture().getId(), e -> true));
    }

    /**
     *  강의별 그룹화 (강의 ID,더미 유저 ID)
     */
    public Map<Long, List<Long>> groupDummyUsersByLecture(Long studentId) {
        return dummyUserRepository.findByStudentId(studentId)
                .stream()
                .collect(Collectors.groupingBy(
                        dummyUser -> dummyUser.getLecture().getId(),
                        Collectors.mapping(DummyUser::getId, Collectors.toList())
                ));
    }
    /**
     *  강의별 수강신청 (병렬)
     */
    public ResponseEntity<String> bulkEnrollDummyUsers(Long studentId) {
        Map<Long, List<Long>> lectureToDummyUsers = groupDummyUsersByLecture(studentId);
        if (lectureToDummyUsers.isEmpty()) {
            return ResponseEntity.ok("No DummyUsers found for Student ID: " + studentId);
        }
        // 강의별 병렬 수강신청
        List<CompletableFuture<Void>> lectureFutures = lectureToDummyUsers.entrySet()
                .stream()
                .map(entry -> CompletableFuture.runAsync(() -> processLectureEnrollments(entry.getKey(), entry.getValue()), executorService))
                .toList();

        CompletableFuture.allOf(lectureFutures.toArray(new CompletableFuture[0])).join();

        return ResponseEntity.ok("Bulk enrollment requests sent.");
    }

    /**
     *  특정 강의 유저들의 수강 신청
     */
    private void processLectureEnrollments(Long lectureId, List<Long> dummyUserIds) {
        List<CompletableFuture<Void>> futures = dummyUserIds.stream()
                .map(dummyUserId -> CompletableFuture.runAsync(() -> HttpEnrollment.request(dummyUserId, lectureId), executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    /**
     *  최종 등록
     */
    @Transactional
    public void enrollDummyUser(Long dummyUserId,Long lectureId) {
        enrollmentProcessor.processEnrollment(dummyUserId, lectureId);
    }

    /**
     *  초기화
     */
    @Transactional
    public void initialize(Long studentId) {
        List<Long> lectureIds = wishedLectureService.getWishedLecturesIdByStudentId(studentId);

        if (!lectureIds.isEmpty()) {
            lectureRepository.bulkInitializeLectures(lectureIds);
            enrollmentRepository.bulkDeleteByLectureIds(lectureIds);
        }
    }

}