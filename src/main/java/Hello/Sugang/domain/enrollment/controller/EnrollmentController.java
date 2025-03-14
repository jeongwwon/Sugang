package Hello.Sugang.domain.enrollment.controller;

import Hello.Sugang.domain.enrollment.service.EnrollmentService;
import Hello.Sugang.domain.enrollment.service.GroupEnrollmentService;
import Hello.Sugang.domain.enrollment.service.SingleEnrollmentService;
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


@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final WishedLectureService wishedLectureService;
    private final GroupEnrollmentService groupEnrollmentService;
    private final SingleEnrollmentService singleEnrollmentService;

    @GetMapping
    public String createForm(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("loginMember");

        List<Lecture> lectures = wishedLectureService.getWishedLecturesByStudentId(student.getId()).stream().map(WishedLecture::getLecture).toList();
        List<Integer> competitons = wishedLectureService.WaitingList(lectures);
        Map<Long, Boolean> enrollmentMap = enrollmentService.findRegistryList(student.getId());

        model.addAttribute("studentId",student.getId());
        model.addAttribute("competitions",competitons);
        model.addAttribute("lectures", lectures);
        model.addAttribute("enrollmentMap", enrollmentMap);

        return "enrollment/wishList";
    }

    /**
     * 개별 Dummy User 를 HTTP 요청으로 수강 신청
     */
    @PostMapping("/dummy/{dummyUserId}/{lectureId}")
    public ResponseEntity<String> enrollDummyUser(@PathVariable Long dummyUserId,@PathVariable Long lectureId) {
        singleEnrollmentService.enrollDummyUser(dummyUserId,lectureId);
        return ResponseEntity.ok("Enrollment request received for DummyUser ID: " + dummyUserId);
    }

    /**
     * 특정 학생의 모든 DummyUser가 개별적으로 HTTP 요청을 보내도록 병렬 요청 실행
     */
    @PostMapping("/dummy/bulk/{studentId}")
    public ResponseEntity<String> bulkEnrollDummyUsers(@PathVariable Long studentId) {
        return groupEnrollmentService.bulkEnrollDummyUsers(studentId);
    }


    @PostMapping("/init")
    public String initializeEnrollment(@RequestParam Long studentId){
        enrollmentService.initialize(studentId);
        return "redirect:/enrollment";
    }

}
