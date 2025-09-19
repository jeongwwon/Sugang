package Hello.Sugang.domain.enrollment.controller;

import Hello.Sugang.domain.enrollment.Enrollment;
import Hello.Sugang.domain.enrollment.Facade.EnrollmentFacade;
import Hello.Sugang.domain.enrollmentLog.EnrollmentLogRepository;
import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.wishedlecture.WishedLecture;
import Hello.Sugang.domain.wishedlecture.WishedLectureService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("enrollment")
public class EnrollmentController {

    private final WishedLectureService wishedLectureService;
    private final EnrollmentLogRepository enrollmentLogRepository;
    private final Map<String, Enrollment> enrollmentStrategies;
    private final EnrollmentFacade enrollmentFacade;

    @GetMapping
    public String createForm(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("loginMember");
        Long studentId=student.getId();

        List<Lecture> lectures = wishedLectureService.getWishedLecturesByStudentId(studentId)
                .stream()
                .map(WishedLecture::getLecture)
                .toList();

        // 경쟁자 수 조회
        List<Integer> competitions = wishedLectureService.WaitingList(lectures);

        // 수강 신청 내역 조회 (Map 대신 Set으로 단순화)
        Set<Long> enrollmentSet = enrollmentLogRepository.findByStudentId(studentId)
                .stream()
                .map(e -> e.getLecture().getId())
                .collect(Collectors.toSet());
        EnrollmentViewDto dto=new EnrollmentViewDto(studentId, lectures, competitions, enrollmentSet);
        model.addAttribute("studentId", dto.getStudentId());
        model.addAttribute("lectures", dto.getLectures());
        model.addAttribute("competitions", dto.getCompetitions());
        model.addAttribute("enrollmentSet", dto.getEnrollmentSet()); // Map 대신 Set

        return "enrollment/wishList";
    }

    @PostMapping({"/{mode}/{studentId}", "/{mode}/{studentId}/{lectureId}"})
    public ResponseEntity<String> enroll(@PathVariable String mode,
                                         @PathVariable Long studentId,
                                         @PathVariable(required = false) Long lectureId) {
        return enrollmentFacade.enroll(mode, studentId, lectureId);
    }

    @PostMapping("/init")
    public String initializeEnrollment(@RequestParam Long studentId){
        enrollmentFacade.initialize(studentId);
        return "redirect:/enrollment";
    }

}
