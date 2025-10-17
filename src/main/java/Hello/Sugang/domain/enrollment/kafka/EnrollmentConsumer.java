package Hello.Sugang.domain.enrollment.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentConsumer {

    private final StringRedisTemplate redisTemplate;

    private static final String SEAT_KEY_PREFIX = "seat:";
    private static final String LECTURE_KEY_PREFIX = "lecture:";
    private static final String ENROLL_KEY_PREFIX = "enroll:";

    //@KafkaListener(topics = "enrollment_v2", groupId = "sugang")
    public void consume(String message) {
        String[] parts = message.split(":");
        Long studentId = Long.valueOf(parts[0]);
        Long lectureId = Long.valueOf(parts[1]);

        String seatKey = SEAT_KEY_PREFIX + lectureId;
        String enrollKey = ENROLL_KEY_PREFIX + lectureId;
        String lectureKey = LECTURE_KEY_PREFIX + lectureId;

        // 좌석 차감
        Long remaining = redisTemplate.opsForValue().decrement(seatKey);
        if (remaining == null || remaining < 0) {
            redisTemplate.opsForValue().increment(seatKey);
            return; // 좌석 없음
        }

        // ZSET에 학생 등록
        double score = System.currentTimeMillis();
        Boolean added = redisTemplate.opsForZSet().add(enrollKey, studentId.toString(), score);
        if (Boolean.FALSE.equals(added)) {
            redisTemplate.opsForValue().increment(seatKey);
            return; // 중복 신청
        }

        // Hash 갱신
        redisTemplate.opsForHash().increment(lectureKey, "remainingSeats", -1);
    }
}

