package Hello.Sugang.domain.batch;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class RedisToMySQLItemReader implements ItemReader<RedisLectureDTO> {

    private final StringRedisTemplate redisTemplate;
    private Iterator<Long> lectureIterator; // 여러 강의 처리 대비용

    @Override
    public RedisLectureDTO read() {
        // 1) 처리할 lecture 목록을 초기화
        if (lectureIterator == null) {
            Set<String> keys = redisTemplate.keys("enroll:*");
            if (keys == null || keys.isEmpty()) return null;
            List<Long> lectures = keys.stream()
                    .map(k -> Long.parseLong(k.replace("enroll:", "")))
                    .collect(Collectors.toList());
            lectureIterator = lectures.iterator();
        }

        // 2) 더 이상 lecture 없으면 종료
        if (!lectureIterator.hasNext()) return null;
        Long lectureId = lectureIterator.next();

        String enrollKey = "enroll:" + lectureId;
        String lastProcessedKey = "last_processed:" + lectureId;

        // 3) 이전 처리 시점 읽기
        double lastTs = Double.parseDouble(
                Optional.ofNullable(redisTemplate.opsForValue().get(lastProcessedKey))
                        .orElse("0")
        );

        // 4) 최신 데이터만 읽기 (ZSET: score = timestamp)
        Set<ZSetOperations.TypedTuple<String>> newRecords =
                redisTemplate.opsForZSet().rangeByScoreWithScores(
                        enrollKey, lastTs + 1, System.currentTimeMillis());

        if (newRecords == null || newRecords.isEmpty()) return read(); // 다음 lecture로 넘어감

        // 5) studentId 리스트 변환
        List<Long> students = newRecords.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .filter(Objects::nonNull)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        // 6) last_processed 갱신
        redisTemplate.opsForValue().set(
                lastProcessedKey,
                String.valueOf(System.currentTimeMillis())
        );

        return new RedisLectureDTO(lectureId, students.size(), students);
    }
}
