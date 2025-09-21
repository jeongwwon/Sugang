package Hello.Sugang.domain.lecture.cache;

import Hello.Sugang.domain.lecture.entity.Lecture;
import Hello.Sugang.domain.lecture.repository.LectureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class LectureCacheScheduler {

    private final LectureRepository lectureRepository;
    private final StringRedisTemplate redisTemplate;
    private final LectureCacheManager cacheManager;

    private static final String LECTURE_KEY_PREFIX = "lecture:";

    @Scheduled(fixedRate = 300000) // 5분마다 실행
    public void checkAndRefreshLectureCache() {
        List<Lecture> lectures = lectureRepository.findAll();

        for (Lecture lecture : lectures) {
            String lectureKey = LECTURE_KEY_PREFIX + lecture.getId();

            Object cachedVersion = redisTemplate.opsForHash().get(lectureKey, "version");
            long dbVersion = lecture.getVersion();

            // 캐시가 없거나 버전이 다르면 갱신
            if (cachedVersion == null ||
                    dbVersion != Long.parseLong(cachedVersion.toString())) {
                cacheManager.refreshLectureCache(lecture.getId());
            }
        }
    }
}
