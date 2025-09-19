package Hello.Sugang.domain.collegeStats.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import Hello.Sugang.domain.collegeStats.repository.CollegeStatsRepository;
import Hello.Sugang.domain.lecture.LectureRepository;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollegeStatsService {
    private final CollegeStatsRepository collegeStatsRepository;

    private final LectureRepository lectureRepository;

    //@Scheduled(cron = "0 */1 * * * *") // 1분마다 실행
    @Transactional
    public void refreshCollegeStats() {
        log.info("Starting college stats refresh at {}", LocalDateTime.now());
        try {
            List<Object[]> schoolDepts = lectureRepository.findDistinctSchoolDepartment();

            for (Object[] row : schoolDepts) {
                String school = (String) row[0];
                String department = (String) row[1];

                int inserted = collegeStatsRepository.insertCollegeStats(school, department);
                log.info("Stats refreshed for {}-{}, inserted {} row(s)", school, department, inserted);
            }
            log.info("College stats refreshed successfully for all schools/departments");
        } catch (Exception e) {
            log.error("Failed to refresh college stats", e);
            throw e;
        }
    }

}
