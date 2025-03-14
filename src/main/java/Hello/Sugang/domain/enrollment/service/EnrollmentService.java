package Hello.Sugang.domain.enrollment.service;


import Hello.Sugang.domain.Dummy.DummyUserRepository;
import Hello.Sugang.domain.enrollment.EnrollmentRepository;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.wishedlecture.WishedLectureService;
import io.micrometer.core.annotation.Timed;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@Timed("my.enrollment")
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final WishedLectureService wishedLectureService;
    private final LectureRepository lectureRepository;

    /**
     *  특정 학생의 등록 여부 확인
     */
    public Map<Long, Boolean> findRegistryList(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId)
                .stream().collect(Collectors.toMap(e -> e.getLecture().getId(), e -> true));
    }
    
    /**
     *  초기화
     */
    @Transactional
    public void initialize(Long studentId) {
        List<Long> lectureIds = wishedLectureService.getWishedLecturesIdByStudentId(studentId);

        if (!lectureIds.isEmpty()) {
            lectureRepository.bulkInitializeLectures(lectureIds);
            enrollmentRepository.bulkDeleteByLectureIds(lectureIds);
        }
    }

}