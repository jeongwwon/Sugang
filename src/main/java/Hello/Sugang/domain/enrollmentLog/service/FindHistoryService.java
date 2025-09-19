package Hello.Sugang.domain.enrollmentLog.service;

import Hello.Sugang.domain.collegeStats.entity.CollegeStats;
import Hello.Sugang.domain.enrollmentLog.DTO.StudentHistoryDto;
import Hello.Sugang.domain.enrollmentLog.EnrollmentLog;
import Hello.Sugang.domain.enrollmentLog.EnrollmentLogRepository;
import Hello.Sugang.domain.lecture.Lecture;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class FindHistoryService {

    private final EnrollmentLogRepository enrollmentLogRepository;

    public List<StudentHistoryDto> findHistory(Long studentId){
        return transferDto(enrollmentLogRepository.findByStudentId(studentId));

    }

    public List<StudentHistoryDto> findHistoryWithFetchJoin(Long studentId){
        return transferDto(enrollmentLogRepository.findWithLectureByStudentId(studentId));
    }
    // 테스트 작성,청사진 캡쳐 -> 인덱스 생성 -> 테스트,청사진 캡쳐
    public List<StudentHistoryDto> findCollegeHistory(String school,String department){
        return transferDto(enrollmentLogRepository.findBySchoolAndDepartment(school,department));
    }
    public List<CollegeStats> findCollegeHistory2(String school, String department){
        return enrollmentLogRepository.findBySchoolAndDepartment2(school,department);
    }

    public List<StudentHistoryDto>transferDto(List<EnrollmentLog> enrollmentLogs){

        List<StudentHistoryDto> list=new ArrayList<>();

        for (EnrollmentLog log : enrollmentLogs) {
            Lecture lecture = log.getLecture();
            list.add(new StudentHistoryDto(
                    log.getStudentId(),
                    lecture.getId(),
                    lecture.getName(),
                    lecture.getSchool(),
                    lecture.getDepartment()
            ));
        }
        return list;
    }
}
