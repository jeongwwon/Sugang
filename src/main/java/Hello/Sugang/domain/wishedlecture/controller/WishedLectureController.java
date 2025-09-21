package Hello.Sugang.domain.wishedlecture.controller;

import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.service.LectureService;
import Hello.Sugang.domain.Dummy.service.DummyUserService;
import Hello.Sugang.domain.student.entity.Student;
import Hello.Sugang.domain.wishedlecture.entity.WishedLecture;
import Hello.Sugang.domain.wishedlecture.service.WishedLectureService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("wishedlecture")
public class WishedLectureController {

    private final LectureService lectureService;
    private final WishedLectureService wishedLectureService;
    private final DummyUserService dummyUserService;

    @GetMapping("/new")
    public String WishedList(Model model, HttpSession session) {
        // 현재 로그인한 학생 정보 가져오기
        Student student = (Student) session.getAttribute("loginMember");

        // 전체 강의 목록 조회
        List<Lecture> lectures = lectureService.findLectures();

        // 해당 학생이 신청한 희망 강의 목록 조회
        List<WishedLecture> wishedLectures = wishedLectureService.getWishedLecturesByStudentId(student.getId());

        // 만약 wishedLectures가 null이면 빈 리스트로 초기화하여 NullPointerException 방지
        if (wishedLectures == null) {
            wishedLectures = new ArrayList<>();
        }

        // Thymeleaf에서 쉽게 접근할 수 있도록 Map 변환 (lectureId → competition 값 매핑)
        Map<Long, Double> wishedLectureMap = wishedLectures.stream()
                .collect(Collectors.toMap(wl -> wl.getLecture().getId(), WishedLecture::getCompetition));

        // 모델에 데이터 추가
        model.addAttribute("lectures", lectures);
        model.addAttribute("wishedLectures", wishedLectures);
        model.addAttribute("wishedLectureMap", wishedLectureMap); // Thymeleaf에서 활용할 Map 추가

        return "wishedlecture/LectureList";
    }


    @PostMapping("new")
    public String addWishedLecture(
            @RequestParam("studentId") Long studentId,
            @RequestParam("lectureId") Long lectureId,
            @RequestParam("competition") double competition) {

        wishedLectureService.saveWishedLecture(studentId,lectureId,competition);
        dummyUserService.createDummyStudents(studentId,lectureId,competition);

        return "redirect:/wishedlecture/new"; // 예제: 강의 목록 페이지로 리디렉트
    }

    @PostMapping("/{id}")
    public String cancelWishedLecture(@PathVariable("id") Long lectureId,HttpSession session) {
        Student student = (Student) session.getAttribute("loginMember");
        wishedLectureService.deleteWishedLecture(lectureId);
        dummyUserService.removeDummyUsersByLectureId(lectureId);
        return "redirect:/wishedlecture/new";
    }

}
