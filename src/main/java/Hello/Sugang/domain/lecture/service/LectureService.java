package Hello.Sugang.domain.lecture.service;

import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.dto.LectureForm;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;

    @Transactional
    public void createLecture(LectureForm lectureForm) {
        Lecture lecture = new Lecture();

        lecture.setLectureTime(lectureForm.getLectureTime());
        lecture.setName(lectureForm.getName());
        lecture.setTotalSeats(lectureForm.getTotalSeats());
        lecture.setRemainingSeats(lectureForm.getTotalSeats());


        lectureRepository.save(lecture);
    }

    @Transactional
    public void updateLecture(Long id, LectureForm lectureForm) {
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 강의를 찾을 수 없습니다. ID: " + id));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // 연도를 2025로 고정
        LocalDateTime lectureDateTime = LocalDateTime.parse(lectureForm.getLectureTime().format(formatter), formatter)
                .withYear(2025);

        lecture.setLectureTime(lectureDateTime);
        lecture.setName(lectureForm.getName());
        lecture.setTotalSeats(lectureForm.getTotalSeats());
        lecture.setRemainingSeats(lectureForm.getTotalSeats());

        // ⚠ 기존 잔여 좌석보다 총 좌석이 줄어들 경우 예외 처리
        if (lectureForm.getTotalSeats() < (lecture.getTotalSeats() - lecture.getRemainingSeats())) {
            throw new IllegalArgumentException("총 좌석 수는 현재 신청된 인원보다 적을 수 없습니다.");
        }

        lecture.setRemainingSeats(lectureForm.getTotalSeats() - (lecture.getTotalSeats() - lecture.getRemainingSeats()));
    }

    public List<Lecture> findLectures() {
        return lectureRepository.findAll();
    }

    public Lecture findOne(Long id) {
        return lectureRepository.findById(id).orElse(null);
    }
}
