package Hello.Sugang.domain.enrollment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service("redis")
@RequiredArgsConstructor
public class RedisEnrollmentService implements Enrollment {

    private final StringRedisTemplate redisTemplate;

    private static final String SEAT_KEY_PREFIX = "seat:";
    private static final String LECTURE_KEY_PREFIX = "lecture:";
    private static final String ENROLL_KEY_PREFIX = "enroll:";

    // Redis에 로드된 Lua Script의 SHA
    private static final String REGISTER_SCRIPT_SHA = "60d7dfe4cfd2887d426a56aa1a9d0ffec5e18e18";

    @Override
    public ResponseEntity<String> register(Long studentId, Long lectureId) {
        String seatKey = SEAT_KEY_PREFIX + lectureId;
        String enrollKey = ENROLL_KEY_PREFIX + lectureId;
        String lectureKey = LECTURE_KEY_PREFIX + lectureId;

        try {
            byte[] resultBytes = redisTemplate.execute((RedisConnection connection) ->
                    connection.evalSha(
                            REGISTER_SCRIPT_SHA.getBytes(StandardCharsets.UTF_8),
                            ReturnType.VALUE,
                            3,
                            seatKey.getBytes(StandardCharsets.UTF_8),
                            enrollKey.getBytes(StandardCharsets.UTF_8),
                            lectureKey.getBytes(StandardCharsets.UTF_8),
                            studentId.toString().getBytes(StandardCharsets.UTF_8),
                            String.valueOf(System.currentTimeMillis()).getBytes(StandardCharsets.UTF_8)
                    )
            );

            String response = (resultBytes == null) ? "" : new String(resultBytes, StandardCharsets.UTF_8);

            switch (response) {
                case "SUCCESS":
                    return ResponseEntity.ok("수강신청 성공: studentId=" + studentId + ", lectureId=" + lectureId);
                case "SOLD_OUT":
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 강의는 마감되었습니다.");
                case "ALREADY_REGISTERED":
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 신청된 사용자입니다.");
                case "NO_SEAT_KEY":
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("좌석 키가 없습니다.");
                default:
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("알 수 없는 오류: " + response);
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Redis Lua 실행 중 오류: " + e.getMessage());
        }
    }
}