package Hello.Sugang.domain.Dummy;

import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.student.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
@Service
@RequiredArgsConstructor
@Slf4j
public class DummyUserService {

    private final StudentRepository studentRepository;
    private final DummyUserRepository dummyUserRepository;
    private final LectureRepository lectureRepository;
    private final EntityManager entityManager;
    @Transactional
    public void createDummyStudents(Long studentId,Long lectureId,double competition) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을수 없습니다."));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을수 없습니다."));

        //생성할 더미 학생 수 계산
        int count = (int) Math.round(competition * lecture.getTotalSeats()); // 더미 학생= 경쟁률 * 정원
        ArrayList<DummyUser> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            DummyUser dummyStudent = new DummyUser(student,lecture);
            list.add(dummyStudent);
        }
        dummyUserRepository.saveAll(list);
    }

    @Transactional
    public void removeDummyUsersByLectureId(Long lectureId) {
        List<Long> dummyUserIds = dummyUserRepository.findIdsByLectureId(lectureId);

        if (!dummyUserIds.isEmpty()) {
            dummyUserRepository.bulkDelete(dummyUserIds);
        }

    }
}
