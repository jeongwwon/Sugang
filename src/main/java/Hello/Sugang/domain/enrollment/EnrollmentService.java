package Hello.Sugang.domain.enrollment;

import Hello.Sugang.domain.Dummy.DummyUser;
import Hello.Sugang.domain.Dummy.DummyUserRepository;
import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.Dummy.DummyUserService;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.student.StudentRepository;
import Hello.Sugang.domain.wishedlecture.WishedLecture;
import Hello.Sugang.domain.wishedlecture.WishedLectureService;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EnrollmentService {

    private final WishedLectureService wishedLectureService;
    private final EnrollmentRepository enrollmentRepository;
    private final DummyUserRepository dummyUserRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;

    private final ExecutorService executorService = Executors.newCachedThreadPool();

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

    @Transactional
    public void initialize(Long studentId){
        List<DummyUser> DummyUsers = dummyUserRepository.findByStudentId(studentId);
        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(studentId);
        for (DummyUser user:DummyUsers){
            user.getLecture().increaseSeats();
            enrollmentRepository.deleteByStudentId(user.getId());
        }
        for (Enrollment enrollment:enrollments){
            enrollmentRepository.delete(enrollment);
        }
    }
    public void autoEnrollLectures(Long studentId) {
        List<DummyUser> dummyUsers = dummyUserRepository.findByStudentId(studentId);

        if (dummyUsers.isEmpty()) {
            log.info("No DummyUser found for studentId: {}", studentId);
            return;
        }

        // 병렬 실행을 위해 CompletableFuture 사용
        List<CompletableFuture<Void>> futures = dummyUsers.stream()
                .map(dummyUser -> CompletableFuture.runAsync(() -> processEnrollment(dummyUser.getLecture(), dummyUser), executorService))
                .toList();

        // 모든 작업이 완료될 때까지 기다림
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

    }


    @Transactional
    private synchronized void processEnrollment(Lecture lecture, Object user) { // Student 또는 DummyUser
        Long studentId;
        if (user instanceof Student) {
            studentId = ((Student) user).getId(); // 실제 학생의 ID
        } else if (user instanceof DummyUser) {
            studentId = ((DummyUser) user).getId(); // DummyUser의 ID
        } else {
            throw new IllegalArgumentException("Invalid user type");
        }

        log.info("Processing enrollment for studentId: {} and lectureId: {}", studentId, lecture.getId());

        // 강의 조회
        Lecture targetLecture = lectureRepository.findById(lecture.getId())
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        // 잔여 좌석 확인
        if (targetLecture.getRemainingSeats() <= 0) {
            log.warn("Lecture {} is full. Enrollment failed for student {}", targetLecture.getId(), studentId);
            return;
        }

        // 이미 수강 신청했는지 확인
        boolean alreadyEnrolled = enrollmentRepository.existsByStudentIdAndLectureId(studentId, targetLecture.getId());
        if (alreadyEnrolled) {
            log.info("Student {} already enrolled in lecture {}", studentId, targetLecture.getId());
            return;
        }

        // ✅ Student 또는 DummyUser의 ID 저장
        Enrollment enrollment = new Enrollment();
        enrollment.setStudentId(studentId);
        enrollment.setLecture(targetLecture);
        enrollmentRepository.save(enrollment);

        // 잔여 좌석 감소
        targetLecture.decreaseSeats();
        lectureRepository.save(targetLecture);

        log.info("Successfully enrolled student {} in lecture {}", studentId, targetLecture.getId());
    }


}
