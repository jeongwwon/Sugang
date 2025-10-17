package Hello.Sugang.domain.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class RedisToMySQLItemProcessor implements ItemProcessor<RedisLectureDTO, RedisLectureDTO> {
    @Override
    public RedisLectureDTO process(RedisLectureDTO item) {
        if (item.getCount() == 0) return null;
        return item;
    }
}
