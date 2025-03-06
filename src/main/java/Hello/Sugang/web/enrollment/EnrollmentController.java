package Hello.Sugang.web.enrollment;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("enrollment")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

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
    @PostMapping("/compete")
    public ResponseEntity<Map<String, String>> compete(@RequestBody CompeteForm request) {
        enrollmentService.autoEnrollLectures(request.getStudentId());

        // JSON 형태로 응답
        Map<String, String> response = new HashMap<>();
        response.put("message", "Enrollment process started");

        return ResponseEntity.ok(response);
    }
    @PostMapping("/init")
    public String initializeEnrollment(@RequestParam Long studentId){
        enrollmentService.initialize(studentId);
        return "redirect:/enrollment";
    }

}
