package Hello.Sugang.domain.enrollment.service;

import Hello.Sugang.domain.Dummy.DummyUser;
import Hello.Sugang.domain.Dummy.DummyUserRepository;
import Hello.Sugang.domain.enrollment.network.HttpEnrollment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupEnrollmentService {
    private final DummyUserRepository dummyUserRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(50); // 병렬 HTTP 요청을 위한 스레드 풀
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
     *  강의별 병렬 신청
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
     *  특정 강의에 속한 유저들의 수강 신청(병렬)
     */
    private void processLectureEnrollments(Long lectureId, List<Long> dummyUserIds) {
        List<CompletableFuture<Void>> futures = dummyUserIds.stream()
                .map(dummyUserId -> CompletableFuture.runAsync(() -> HttpEnrollment.request(dummyUserId, lectureId), executorService))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }
}
