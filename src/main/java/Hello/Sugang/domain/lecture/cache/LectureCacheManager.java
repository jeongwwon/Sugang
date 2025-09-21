package Hello.Sugang.domain.lecture.cache;

import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class LectureCacheManager {

    private final StringRedisTemplate redisTemplate;
    private final LectureRepository lectureRepository;

    private static final String SEAT_KEY_PREFIX = "seat:";
    private static final String LECTURE_KEY_PREFIX = "lecture:";
    private static final String ENROLL_KEY_PREFIX = "enroll:";

    /**
     * 강의 캐시 무효화 + 최신 데이터 갱신
     */
    public void refreshLectureCache(Long lectureId) {
        String seatKey = SEAT_KEY_PREFIX + lectureId;
        String lectureKey = LECTURE_KEY_PREFIX + lectureId;
        String enrollKey = ENROLL_KEY_PREFIX + lectureId;

        // 기존 캐시 제거
        redisTemplate.delete(seatKey);
        redisTemplate.delete(lectureKey);
        redisTemplate.delete(enrollKey);

        // DB 최신 데이터 조회
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("Lecture not found"));

        // 좌석 수 저장
        redisTemplate.opsForValue().set(seatKey, String.valueOf(lecture.getRemainingSeats()));

        // 강의 정보 저장 (버전 포함)
        Map<String, String> lectureMap = new HashMap<>();
        lectureMap.put("id", lecture.getId().toString());
        lectureMap.put("school", lecture.getSchool());
        lectureMap.put("department", lecture.getDepartment());
        lectureMap.put("name", lecture.getName());
        lectureMap.put("lectureTime", lecture.getLectureTime().toString());
        lectureMap.put("totalSeats", String.valueOf(lecture.getTotalSeats()));
        lectureMap.put("remainingSeats", String.valueOf(lecture.getRemainingSeats()));
        lectureMap.put("version", String.valueOf(lecture.getVersion()));

        redisTemplate.opsForHash().putAll(lectureKey, lectureMap);

        System.out.printf("[Redis Cache Refresh] lectureId=%d (version=%d) 갱신 완료%n",
                lectureId, lecture.getVersion());
    }
}
