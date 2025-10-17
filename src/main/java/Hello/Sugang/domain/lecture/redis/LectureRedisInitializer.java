package Hello.Sugang.domain.lecture.redis;

import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

//@Component
@RequiredArgsConstructor
public class LectureRedisInitializer implements ApplicationRunner {

    private final LectureRepository lectureRepository;
    private final StringRedisTemplate redisTemplate;

    private static final String SEAT_KEY_PREFIX = "seat:";
    private static final String LECTURE_KEY_PREFIX = "lecture:";
    private static final String ENROLL_KEY_PREFIX = "enroll:";

    //@Override
    public void run(ApplicationArguments args) {
        // lectureId=1번만 가져오기
        Lecture lecture = lectureRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("Lecture 1 not found"));

        String seatKey = SEAT_KEY_PREFIX + lecture.getId();
        String lectureKey = LECTURE_KEY_PREFIX + lecture.getId();
        String enrollKey = ENROLL_KEY_PREFIX + lecture.getId();

        redisTemplate.delete(seatKey);
        redisTemplate.delete(lectureKey);
        redisTemplate.delete(enrollKey);

        // 좌석 수 초기화
        redisTemplate.opsForValue().set(seatKey, String.valueOf(lecture.getRemainingSeats()));

        // 강의 전체 정보 Hash로 저장
        Map<String, String> lectureMap = new HashMap<>();
        lectureMap.put("id", lecture.getId().toString());
        lectureMap.put("school", lecture.getSchool());
        lectureMap.put("department", lecture.getDepartment());
        lectureMap.put("name", lecture.getName());
        lectureMap.put("lectureTime", lecture.getLectureTime().toString());
        lectureMap.put("totalSeats", String.valueOf(lecture.getTotalSeats()));
        lectureMap.put("remainingSeats", String.valueOf(lecture.getRemainingSeats()));

        redisTemplate.opsForHash().putAll(lectureKey, lectureMap);

        System.out.println("[Redis Init] lectureId=1 초기화 완료");
    }
}