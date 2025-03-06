package Hello.Sugang.domain.lecture;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@Slf4j
public class Lecture {
    @Id @GeneratedValue
    @Column(name = "lecture_id")
    private Long id;
    @NotEmpty
    private String name;
    @Column(name = "lecture_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lectureTime;
    private int remainingSeats;
    @NotNull
    private int totalSeats;

    // 좌석 감소 (수강 신청 시 사용)
    public void decreaseSeats() {
        if (remainingSeats > 0) {
            this.remainingSeats -= 1;
        } else {
            log.info("수강 신청 불가: 잔여 좌석이 없습니다.");
        }
    }

    // 좌석 증가 (취소 시 사용)
    public void increaseSeats() {
        if (remainingSeats < totalSeats) {
            this.remainingSeats += 1;
        } else {
            log.info("잔여 좌석이 총 좌석 수를 초과할 수 없습니다.");
        }
    }
}
