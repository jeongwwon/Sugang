package Hello.Sugang.web.enrollment;

import Hello.Sugang.domain.Dummy.DummyUser;
import Hello.Sugang.domain.enrollment.CompeteForm;
import Hello.Sugang.domain.enrollment.EnrollmentService;
import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.student.Student;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping
    public String createForm(Model model, HttpSession session) {
        Student student = (Student) session.getAttribute("loginMember");

        List<Lecture> lectures = enrollmentService.findLecturesByWished(student.getId());
        Map<Long, Boolean> enrollmentMap = enrollmentService.findRegistryList(student.getId());
        List<Integer> competitons = enrollmentService.findReservation(lectures);
        model.addAttribute("studentId",student.getId());
        model.addAttribute("competitions",competitons);
        model.addAttribute("lectures", lectures);
        model.addAttribute("enrollmentMap", enrollmentMap);

        return "enrollment/wishList";
    }

    /**
     * 개별 DummyUser를 HTTP 요청으로 수강 신청
     */
    @PostMapping("/dummy/{dummyUserId}/{lectureId}")
    public ResponseEntity<String> enrollDummyUser(@PathVariable Long dummyUserId,@PathVariable Long lectureId) {
        log.info("Received enrollment request for DummyUser ID: {}", dummyUserId);
        enrollmentService.enrollDummyUser(dummyUserId,lectureId);
        return ResponseEntity.ok("Enrollment request received for DummyUser ID: " + dummyUserId);
    }

    /**
     * 특정 학생의 모든 DummyUser가 개별적으로 HTTP 요청을 보내도록 병렬 요청 실행
     */
    @PostMapping("/dummy/bulk/{studentId}")
    public ResponseEntity<String> bulkEnrollDummyUsers(@PathVariable Long studentId) {
        log.info("Received bulk enrollment request for Student ID: {}", studentId);
        return enrollmentService.bulkEnrollDummyUsers(studentId);
    }


    @PostMapping("/init")
    public String initializeEnrollment(@RequestParam Long studentId){
        enrollmentService.initialize(studentId);
        return "redirect:/enrollment";
    }

}
