package Hello.Sugang.domain.enrollment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service("redis")
@RequiredArgsConstructor
public class RedisEnrollmentService implements Enrollment {

    private final StringRedisTemplate redisTemplate;  // Spring Data Redis

    private static final String SEAT_KEY_PREFIX = "seat:";
    private static final String LECTURE_KEY_PREFIX = "lecture:";
    private static final String ENROLL_KEY_PREFIX = "enroll:";
    @Override
    public ResponseEntity<String> register(Long studentId, Long lectureId) {
        String seatKey = SEAT_KEY_PREFIX + lectureId;
        String enrollKey = ENROLL_KEY_PREFIX + lectureId;
        String lectureKey = LECTURE_KEY_PREFIX + lectureId;

        // 1. 좌석 유무 확인
        if (!Boolean.TRUE.equals(redisTemplate.hasKey(seatKey))) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("좌석 키가 없습니다. 초기화가 되었는지 확인하세요.");
        }
        // 2. 잔여 좌석 차감 (원자적 연산)
        Long remaining = redisTemplate.opsForValue().decrement(seatKey);

        if (remaining == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("좌석 정보가 존재하지 않습니다.");
        }

        if (remaining < 0) {
            // 좌석 초과 → 롤백
            redisTemplate.opsForValue().increment(seatKey);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 강의는 마감되었습니다.");
        }

        // 3. SortedSet에 studentId 등록 (score = timestamp)
        double score = System.currentTimeMillis();
        Boolean added = redisTemplate.opsForZSet().add(enrollKey, studentId.toString(), score);

        if (Boolean.FALSE.equals(added)) {
            // 만약 중복으로 add 실패하면 좌석 롤백
            redisTemplate.opsForValue().increment(seatKey);
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 신청된 사용자입니다.");
        }

        // 4. lecture Hash의 remainingSeats 값도 동기화
        redisTemplate.opsForHash().increment(lectureKey, "remainingSeats", -1);

        return ResponseEntity.ok("수강신청 성공: studentId=" + studentId + ", lectureId=" + lectureId);
    }
}

