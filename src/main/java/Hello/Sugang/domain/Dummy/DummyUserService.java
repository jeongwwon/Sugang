package Hello.Sugang.domain.Dummy;

import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.student.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
@Service
@RequiredArgsConstructor
public class DummyUserService {

    private final StudentRepository studentRepository;
    private final DummyUserRepository dummyUserRepository;
    private final LectureRepository lectureRepository;

    private final AtomicLong dummyIdSequence = new AtomicLong(9999L);

    @Transactional
    public void createDummyStudents(Long studentId,Long lectureId,double competition) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Lecture not found"));

        //생성할 더미 학생 수 계산
        int count = (int) Math.round(competition * lecture.getTotalSeats()); // 예제: 경쟁률 * 10명을 생성

        for (int i = 0; i < count; i++) {
            DummyUser dummyStudent = new DummyUser(student,lecture);
            dummyUserRepository.save(dummyStudent);
        }
    }

    @Transactional
    public void removeDummyUsersByLecture(Long studentId, Long lectureId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        List<DummyUser> findDummyUsers = dummyUserRepository.findByStudentId(studentId);

        for (DummyUser dummyUser:findDummyUsers){
            dummyUserRepository.delete(dummyUser);
        }
    }
}
