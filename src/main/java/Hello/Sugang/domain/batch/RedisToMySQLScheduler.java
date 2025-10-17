package Hello.Sugang.domain.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class RedisToMySQLScheduler {

    private final JobLauncher jobLauncher;
    private final Job redisToMySQLJob;

    // 5분마다 실행
    @Scheduled(cron = "0 */1 * * * *")
    public void runRedisToMySQLJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis()) // 중복 방지용
                    .toJobParameters();

            jobLauncher.run(redisToMySQLJob, params);
            System.out.println("[RedisToMySQLJob] 실행 완료: " + LocalDateTime.now());

        } catch (Exception e) {
            System.err.println("[RedisToMySQLJob] 실행 실패: " + e.getMessage());
        }
    }
}
