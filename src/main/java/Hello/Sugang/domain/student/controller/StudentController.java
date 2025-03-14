package Hello.Sugang.domain.student.controller;

import Hello.Sugang.domain.student.Difficulty;
import Hello.Sugang.domain.student.DifficultyForm;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.student.StudentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/students")
@Slf4j
public class StudentController {

    private final StudentRepository studentRepository;

    @GetMapping("/add")
    public String addForm(@ModelAttribute("student")Student student){
        return "students/addStudentForm";
    }

    @PostMapping("/add")
    @Transactional
    public String save(@Validated @ModelAttribute Student student, BindingResult bindingResult){
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "duplicate.student.email");
        }
        if (bindingResult.hasErrors()) {
            return "students/addStudentForm";
        }
        studentRepository.save(student);
        return "redirect:/";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Student> existingStudent = studentRepository.findById(id);
        if (existingStudent.isEmpty()) {
            return "redirect:/"; // 존재하지 않는 학생이면 홈으로 리디렉트
        }
        model.addAttribute("student",existingStudent.get());
        return "students/editStudentForm"; // 수정 폼 페이지
    }

    @PostMapping("/{id}/edit")
    @Transactional
    public String updateStudent(@PathVariable Long id, @Validated @ModelAttribute DifficultyForm difficultyForm, BindingResult bindingResult) {
        Optional<Student> existingStudentOpt = studentRepository.findById(id);

        if (existingStudentOpt.isEmpty()) {
            return "redirect:/"; // 존재하지 않는 학생이면 홈으로 리디렉트
        }
        Student existingStudent = existingStudentOpt.get();
        if (bindingResult.hasErrors()) {
            log.warn("검증 오류 발생!");
            bindingResult.getAllErrors().forEach(error -> log.warn(error.toString()));
            return "students/editStudentForm"; // 검증 오류 시 다시 수정 폼으로
        }

        // 기존 객체의 값 유지하면서 필요한 정보만 변경
        existingStudent.setDifficulty(difficultyForm.getDifficulty()); // 난이도만 수정 가능
        return "redirect:/"; // 수정 후 홈으로 이동
    }
}
