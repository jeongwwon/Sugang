package Hello.Sugang.domain.enrollmentLog.controller;

import Hello.Sugang.domain.enrollmentLog.dto.StudentHistoryDto;
import Hello.Sugang.domain.enrollmentLog.service.FindHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("enrollmentLog")
public class EnrollmentLogController {

    private final FindHistoryService findHistoryService;
    // N+1 문제 코드
    @GetMapping("/{studentId}/bad")
    public ResponseEntity<List<StudentHistoryDto>> getStudentHistory(@PathVariable Long studentId){
        return ResponseEntity.ok(findHistoryService.findHistory(studentId));
    }

    // Fetch Join 코드
    @GetMapping("/{studentId}/fetch")
    public ResponseEntity<List<StudentHistoryDto>> getStudentHistory2(@PathVariable Long studentId){
        return ResponseEntity.ok(findHistoryService.findHistoryWithFetchJoin(studentId));
    }

    // 단과대,학과별 조회 (느림)
    @GetMapping("/{school}/{department}")
    public ResponseEntity<List<StudentHistoryDto>> getCollegeHistory(@PathVariable String school,@PathVariable String department){
        return ResponseEntity.ok(findHistoryService.findCollegeHistory(school,department));
    }

}
