package Hello.Sugang.domain.lecture;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class LectureForm {
    @NotEmpty(message = "강의명은 필수 입력 항목입니다.")
    private String name;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lectureTime;
    @Min(1)
    private int totalSeats;//=new AtomicInteger(0);
    public LectureForm(){
        this.lectureTime = LocalDateTime.now().withYear(2025); // 연도 2025로 고정
    }
}
