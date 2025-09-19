package Hello.Sugang.domain.enrollmentLog.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class StudentHistoryDto {
    private Long studentId;
    private Long lectureId;
    private String name;
    private String school;
    private String department;
}
