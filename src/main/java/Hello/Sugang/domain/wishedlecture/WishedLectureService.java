package Hello.Sugang.domain.wishedlecture;

import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.Dummy.DummyUserService;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.student.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishedLectureService {

    private final WishedLectureRepository wishedLectureRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;
    private final DummyUserService dummyUserService;

    public List<WishedLecture> getWishedLecturesByStudentId(Long studentId){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        return wishedLectureRepository.findByStudent(student);
    }
    public WishedLecture getWishedLecturesByLectureId(Long lectureId){
        return wishedLectureRepository.findByLectureId(lectureId);
    }
    @Transactional
    public void saveWishedLecture(Long studentId, Long lectureId,double competition) {
        // 강의 찾기
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다."));

        // 학생 찾기
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        // WishedLecture 생성 및 연관 관계 설정 (편의 메서드 사용)
        WishedLecture wishedLecture = new WishedLecture();

        wishedLecture.setLectureAndStudent(student, lecture,competition);
        // 데이터 저장
        wishedLectureRepository.save(wishedLecture);
    }
    @Transactional
    public void deleteWishedLecture(Long LectureId){
        WishedLecture wishedLecture = wishedLectureRepository.findByLectureId(LectureId);

        wishedLectureRepository.delete(wishedLecture);
    }

}
