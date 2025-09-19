package Hello.Sugang.domain.enrollment.service;


import Hello.Sugang.domain.Dummy.service.enrollment.DummyParallelEnrollmentService;
import Hello.Sugang.domain.Dummy.service.grouping.FindDummyUserService;
import Hello.Sugang.domain.enrollment.Enrollment;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.student.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service("practice")
@RequiredArgsConstructor
public class PracticeEnrollmentService implements Enrollment {

    private final FindDummyUserService dummygroupingService;
    private final DummyParallelEnrollmentService dummyParallelEnrollmentService;
    private final StudentRepository studentRepository;

    @Override
    public ResponseEntity<String> register(Long studentId,Long lectureId) {
        Optional<Student> student=studentRepository.findById(studentId);
        /**
         * 1.Lecture별 더미 유저들 그룹핑
         */
        Map<Long, List<Long>> lectureToDummyUsers = dummygroupingService.groupDummyUsersByLecture(studentId);
        if (lectureToDummyUsers.isEmpty()) {
            return ResponseEntity.ok("No DummyUsers found for Student ID: " + studentId);
        }

        /**
         * 2.강의별 가짜 유저들의 수강 신청(병렬)
         */
        lectureToDummyUsers.forEach(
                (lecture, dummyUserIds) -> dummyParallelEnrollmentService.enrollLecture(lecture, dummyUserIds,student.get().getDifficulty())
        );

        return ResponseEntity.ok("Bulk enrollment requests sent.");
    }
}
