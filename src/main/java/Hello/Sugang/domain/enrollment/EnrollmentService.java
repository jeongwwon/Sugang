package Hello.Sugang.domain.enrollment;


import Hello.Sugang.domain.Dummy.DummyUser;
import Hello.Sugang.domain.Dummy.DummyUserRepository;
import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.lecture.LectureService;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.wishedlecture.WishedLectureService;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.reactive.function.client.WebClient;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import Hello.Sugang.domain.wishedlecture.WishedLecture;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.time.Duration;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Service
@Slf4j
@Timed("my.enrollment")
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final DummyUserRepository dummyUserRepository;
    private final WishedLectureService wishedLectureService;
    private final LectureRepository lectureRepository;
    private final LectureService lectureService;
    private final RestTemplate restTemplate = new RestTemplate(); // HTTP 요청을 보내기 위한 RestTemplate
    private final ExecutorService executorService = Executors.newFixedThreadPool(50); // 병렬 HTTP 요청을 위한 스레드 풀
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8080").build();

    /**
     * 특정 학생의 모든 DummyUser ID 목록을 가져옴
     */

    public List<Lecture> findLecturesByWished(Long studentId) {
        return wishedLectureService.getWishedLecturesByStudentId(studentId)
                .stream().map(WishedLecture::getLecture).toList();
    }

    public Map<Long, Boolean> findRegistryList(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream().collect(Collectors.toMap(e -> e.getLecture().getId(), e -> true));
    }
    public List<Integer> findReservation(List<Lecture> lectures){
        ArrayList<Integer> list = new ArrayList<>();
        for (Lecture lr:lectures){
            list.add(lr.getTotalSeats()*(int)wishedLectureService.getWishedLecturesByLectureId(lr.getId()).getCompetition());
        }
        return list;
    }
    // lecture 별로 그룹화
    public Map<Long, List<Long>> getDummyUserIdsByLecture(Long studentId) {
        return dummyUserRepository.findByStudentId(studentId)
                .stream()
                .collect(Collectors.groupingBy(
                        dummyUser -> dummyUser.getLecture().getId(),
                        Collectors.mapping(DummyUser::getId, Collectors.toList())
                ));
    }
    /**
     * 모든 DummyUser의 수강 신청
     */
    public ResponseEntity<String> bulkEnrollDummyUsers(Long studentId) {
        Map<Long, List<Long>> lectureToDummyUsers = getDummyUserIdsByLecture(studentId);

        if (lectureToDummyUsers.isEmpty()) {
            log.info("No DummyUsers found for Student ID: {}", studentId);
            return ResponseEntity.ok("No DummyUsers found for Student ID: " + studentId);
        }

        // 병렬 실행: lecture_id 별로 병렬 실행되도록 설정
        List<CompletableFuture<Void>> lectureFutures = lectureToDummyUsers.entrySet()
                .stream()
                .map(entry -> CompletableFuture.runAsync(() -> {
                    Long lectureId = entry.getKey();
                    List<Long> dummyUserIds = entry.getValue();
                    log.info("Processing {} DummyUsers for Lecture ID: {}", dummyUserIds.size(), lectureId);

                    // DummyUser ID별 개별 실행 (병렬 실행)
                    List<CompletableFuture<Void>> futures = dummyUserIds.stream()
                            .map(dummyUserId -> CompletableFuture.runAsync(() -> sendEnrollmentRequest(dummyUserId,lectureId), executorService))
                            .toList();

                    // 해당 lecture_id 내에서 모든 요청이 완료될 때까지 대기
                    CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                    log.info("Finished processing all DummyUsers for Lecture ID: {}", lectureId);
                }, executorService))
                .toList();

        // 모든 lecture_id 처리가 완료될 때까지 대기
        CompletableFuture.allOf(lectureFutures.toArray(new CompletableFuture[0])).join();

        return ResponseEntity.ok("Bulk enrollment requests sent for Student ID: " + studentId);
    }

    /**
     * 특정 DummyUser에 대해 HTTP 요청을 보내는 메서드
     */
    public void sendEnrollmentRequest(Long dummyUserId,Long lectureId) {
        String url = "http://localhost:8080/enrollment/dummy/" + dummyUserId+"/"+lectureId;
        try {
            log.info("Sending HTTP request to enroll DummyUser ID: {}", dummyUserId);
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
            log.info("Response: {}", response.getBody());
        } catch (Exception e) {
            log.error("Failed to send enrollment request for DummyUser ID: {}", dummyUserId, e);
        }
    }
    /**
     * DummyUser의 개별 HTTP 수강 신청
     */
    @Transactional
    public void enrollDummyUser(Long dummyUserId,Long lectureId) {

        Lecture targetLecture = lectureRepository.findByIdForUpdate(lectureId);

        if (targetLecture.getRemainingSeats() <= 0) {
            log.warn("Lecture {} is full",targetLecture.getId());
            return;
        }
        processEnrollment(dummyUserId, lectureId , targetLecture);
    }

    /**
     * 수강 신청 로직
     */
    @Transactional
    public void processEnrollment(Long studentId, Long lectureId,Lecture targetLecture) {
        log.info("Processing enrollment for studentId: {} and lectureId: {}", studentId, lectureId);

        if (targetLecture.getRemainingSeats() <= 0) {
            log.warn("Lecture {} is full. Enrollment failed for student {}", targetLecture.getId(), studentId);
            return;
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setLecture(targetLecture);
        enrollmentRepository.save(enrollment);

        targetLecture.decreaseSeats();
    }

    @Transactional
    public void initialize(Long studentId) {
        List<Long> lectureIds = wishedLectureService.getWishedLecturesIdByStudentId(studentId);

        if (!lectureIds.isEmpty()) {
            lectureRepository.bulkInitializeLectures(lectureIds);
            enrollmentRepository.bulkDeleteByLectureIds(lectureIds);
        }
    }
//    @Transactional
//        public void initialize(Long studentId){
//            List<DummyUser> DummyUsers = dummyUserRepository.findByStudentId(studentId);
//            List<Long> lectureIds = wishedLectureService.getWishedLecturesIdByStudentId(studentId);
//            for (Long id:lectureIds){
//                lectureService.initializeLecture(id);
//                enrollmentRepository.deleteByLectureId(id);
//            }
//    }

}