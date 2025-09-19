package Hello.Sugang.domain.lecture.controller;

import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureForm;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.lecture.service.LectureService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/lecture")
public class LectureController {
    private final LectureRepository lectureRepository;
    private final LectureService lectureService;

    @GetMapping("/new")
    public String createForm(@ModelAttribute("form") LectureForm lectureForm) {

        return "lecture/createLectureForm";
    }

    @PostMapping("/new")
    public String create(@Validated @ModelAttribute("form") LectureForm lectureForm, BindingResult bindingResult,Model model){
        if(bindingResult.hasErrors()){
            model.addAttribute("form", lectureForm);
            return "lecture/createLectureForm";
        }
        lectureService.createLecture(lectureForm);
        return "redirect:/lecture";
    }

    @GetMapping
    public String list(Model model){
        List<Lecture>lectures=lectureService.findLectures();
        model.addAttribute("lectures",lectures);
        return "lecture/LectureList";
    }

    @GetMapping("/{lectureId}/edit")
    public String updateLectureForm(@PathVariable("lectureId") Long lectureId, Model model) {
        Lecture lecture = lectureService.findOne(lectureId);
        if (lecture == null) {
            throw new IllegalArgumentException("해당 강의를 찾을 수 없습니다. ID: " + lectureId);
        }

        // Lecture 객체를 LectureForm으로 변환하여 폼에 전달
        LectureForm lectureForm = new LectureForm();
        lectureForm.setName(lecture.getName());
        lectureForm.setTotalSeats(lecture.getTotalSeats());

        // 연도를 2025로 고정하여 LocalDateTime으로 설정
        LocalDateTime lectureTime = lecture.getLectureTime().withYear(2025);
        lectureForm.setLectureTime(lectureTime);

        model.addAttribute("form", lectureForm);
        return "lecture/updateLectureForm"; // 업데이트 폼 뷰로 이동
    }

    @PostMapping("/{lectureId}/edit")
    public String updateLecture(@PathVariable("lectureId") Long lectureId,@Validated @ModelAttribute LectureForm form,BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return "lecture/updateLectureForm";
        }
        lectureService.updateLecture(lectureId,form);
        return "redirect:/lecture";
    }
}
