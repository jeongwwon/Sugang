package Hello.Sugang.domain.wishedlecture.service;

import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import Hello.Sugang.domain.student.entity.Student;
import Hello.Sugang.domain.student.repository.StudentRepository;
import Hello.Sugang.domain.wishedlecture.repository.WishedLectureRepository;
import Hello.Sugang.domain.wishedlecture.entity.WishedLecture;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class WishedLectureService {

    private final WishedLectureRepository wishedLectureRepository;
    private final LectureRepository lectureRepository;
    private final StudentRepository studentRepository;

    public List<WishedLecture> getWishedLecturesByStudentId(Long studentId){
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 학생을 찾을 수 없습니다."));

        return wishedLectureRepository.findByStudent(student);
    }

    public List<Long> getWishedLecturesIdByStudentId(Long studentId) {
        return wishedLectureRepository.getWishedLectureIdsByStudentId(studentId);
    }

    /**
     *  강의별 수강신청 대기 인원
     */
    public List<Integer> WaitingList(List<Lecture> lectures){
        ArrayList<Integer> list = new ArrayList<>();
        for (Lecture lr:lectures){
            list.add(lr.getTotalSeats()*(int)getWishedLecturesByLectureId(lr.getId()).getCompetition());
        }
        return list;
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

        if (wishedLecture==null){
            log.info("삭제할 강의가 존재하지 않습니다");
            return;
        }
        wishedLectureRepository.delete(wishedLecture);
    }

}
