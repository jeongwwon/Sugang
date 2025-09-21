package Hello.Sugang.domain.Dummy.service;

import Hello.Sugang.domain.Dummy.repository.DummyUserRepository;
import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import Hello.Sugang.domain.student.entity.Student;
import Hello.Sugang.domain.student.repository.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DummyUserService {

    private final StudentRepository studentRepository;
    private final DummyUserRepository dummyUserRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public void createDummyStudents(Long studentId,Long lectureId,double competition) {

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을수 없습니다."));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을수 없습니다."));

        //생성할 더미 학생 수 계산
        int count = (int) Math.round(competition * lecture.getTotalSeats()); // 더미 학생= 경쟁률 * 정원
        dummyUserRepository.bulkInsert(studentId, lectureId, count);
    }

    @Transactional
    public void removeDummyUsersByLectureId(Long lectureId) {
        List<Long> dummyUserIds = dummyUserRepository.findIdsByLectureId(lectureId);

        if (!dummyUserIds.isEmpty()) {
            dummyUserRepository.bulkDelete(dummyUserIds);
        }

    }
}
