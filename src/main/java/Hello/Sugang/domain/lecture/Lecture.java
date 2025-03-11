package Hello.Sugang.domain.lecture;

import Hello.converter.AtomicIntegerConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@Slf4j
public class Lecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lecture_id")
    private Long id;

    @NotEmpty
    private String name;

    @Column(name = "lecture_time", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime lectureTime;

    @NotNull
    @Min(0) // 최소값 검증 추가
    private int remainingSeats;

    @NotNull
    @Min(1) // 최소 1개의 좌석이 있어야 함
    private int totalSeats;

    // ✅ 좌석 감소 (수강 신청 시 사용)
    public synchronized boolean decreaseSeats() {
        if (remainingSeats > 0) {
            this.remainingSeats--;
            return true;
        } else {
            log.info("수강 신청 불가: 잔여 좌석이 없습니다.");
            return false;
        }
    }

    // ✅ 좌석 증가 (취소 시 사용)
    public synchronized void increaseSeats() {
        if (remainingSeats < totalSeats) {
            this.remainingSeats++;
        } else {
            log.info("잔여 좌석이 총 좌석 수를 초과할 수 없습니다.");
        }
    }
    public void init(){
        this.remainingSeats=totalSeats;
    }
}
