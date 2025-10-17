package Hello.Sugang.domain.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class RedisToMySQLJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RedisToMySQLItemReader reader;
    private final RedisToMySQLItemProcessor processor;
    private final RedisToMySQLItemWriter writer;

    @Bean
    public Job redisToMySQLJob() {
        return new JobBuilder("redisToMySQLJob", jobRepository)
                .start(syncStep())
                .build();
    }

    @Bean
    public Step syncStep() {
        return new StepBuilder("syncStep", jobRepository)
                .<RedisLectureDTO, RedisLectureDTO>chunk(10, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
}
