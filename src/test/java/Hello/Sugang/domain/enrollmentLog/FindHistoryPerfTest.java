package Hello.Sugang.domain.enrollmentLog;

import Hello.Sugang.domain.QueryType;
import Hello.Sugang.domain.config.RequestContext;
import Hello.Sugang.domain.config.RequestContextHolder;
import Hello.Sugang.domain.enrollmentLog.service.FindHistoryService;
import Hello.Sugang.domain.enrollmentLog.dto.StudentHistoryDto;
import jakarta.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class FindHistoryPerfTest {
    @Autowired
    private FindHistoryService findHistoryService;

    @Autowired
    private EntityManager em;

    private Statistics statistics;

    @BeforeEach
    void initStatistics() {
        Session session = em.unwrap(Session.class);
        SessionFactory sessionFactory = session.getSessionFactory();
        this.statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();
    }
    @DisplayName("N+1 vs FetchJoin 수강신청내역 성능 비교")
    @Test
    void compareQueryCountsAndTime_manualContext() {
        Long studentId = 1L;

        // RequestContext 수동 초기화 (Interceptor 대신)
        RequestContextHolder.initContext(
                RequestContext.builder()
                        .httpMethod("GET")
                        .bestMatchPath("/enrollmentLog/{id}")
                        .build()
        );

        // N+1 버전
        StopWatch stopWatch1 = new StopWatch();
        stopWatch1.start();
        List<StudentHistoryDto> history1 = findHistoryService.findHistory(studentId);
        stopWatch1.stop();

        //현재 컨텍스트 저장
        Map<QueryType,Integer> queryCount1 = new HashMap<>(RequestContextHolder.getContext().getQueryCountByType());

        System.out.println("현재 쿼리:");
        System.out.println("==== N+1 버전 ====");
        RequestContextHolder.getContext().getQueryCountByType().forEach((type, count) ->
                System.out.println(type + " : " + count));
        System.out.println("조회된 DTO 수: " + history1.size());
        System.out.println("응답 시간(ms): " + stopWatch1.getTotalTimeMillis());

        // 쿼리 카운트 리셋
        RequestContextHolder.initContext(
                RequestContext.builder()
                        .httpMethod("GET")
                        .bestMatchPath("/enrollmentLog/{id}")
                        .build()
        );

        // Fetch Join 버전
        StopWatch stopWatch2 = new StopWatch();
        stopWatch2.start();
        List<StudentHistoryDto> history2 = findHistoryService.findHistoryWithFetchJoin(studentId);
        stopWatch2.stop();

        //현재 컨텍스트 저장2
        Map<QueryType,Integer> queryCount2 = new HashMap<>(RequestContextHolder.getContext().getQueryCountByType());

        System.out.println("==== Fetch Join 버전 ====");
        RequestContextHolder.getContext().getQueryCountByType().forEach((type, count) ->
                System.out.println(type + " : " + count));
        System.out.println("조회된 DTO 수: " + history2.size());
        System.out.println("응답 시간(ms): " + stopWatch2.getTotalTimeMillis());

        // 테스트 종료 후 ThreadLocal 정리
        assertThat(history2).hasSize(history1.size());
        // 2) Fetch Join은 Select 쿼리 수가 더 적어야 함
        int selectCount1 = queryCount1.getOrDefault(QueryType.SELECT, 0);
        int selectCount2 = queryCount2.getOrDefault(QueryType.SELECT, 0);

        assertThat(selectCount2)
                .as("Fetch Join은 N+1 쿼리보다 SELECT 수가 적어야 함")
                .isLessThan(selectCount1);
        RequestContextHolder.clear();
    }
    @Test
    @DisplayName("집계 테이블 생성 전 후 성능 비교")
    void checkCollegeHistoryResponseTime(){
        String school="School2";
        String department="Department2";
        StopWatch stopWatch1 = new StopWatch();
        stopWatch1.start();
        findHistoryService.findCollegeHistory(school,department);
        stopWatch1.stop();

        // 집계 테이블 조회
        StopWatch stopWatch2 = new StopWatch();

        stopWatch2.start();
        findHistoryService.findCollegeHistory2(school,department);
        stopWatch2.stop();

        System.out.println("응답 시간(ms): " + stopWatch1.getTotalTimeMillis());
        System.out.println("집계 테이블 생성 후 응답 시간(ms): " + stopWatch2.getTotalTimeMillis());
    }
}
