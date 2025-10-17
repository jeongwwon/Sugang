package Hello.Sugang.domain.batch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedisLectureDTO {
    private Long lectureId;
    private int count;
    private List<Long> enrolledStudents;
}
