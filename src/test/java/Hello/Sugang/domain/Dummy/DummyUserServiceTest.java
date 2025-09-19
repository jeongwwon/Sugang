package Hello.Sugang.domain.Dummy;

import Hello.Sugang.domain.lecture.Lecture;
import Hello.Sugang.domain.lecture.LectureRepository;
import Hello.Sugang.domain.student.Student;
import Hello.Sugang.domain.student.StudentRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.util.StopWatch;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@SpringBootTest
class DummyUserServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private DummyUserRepository dummyUserRepository;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private StudentRepository studentRepository;

    private final int TOTAL_DUMMY_USERS = 1000; // 생성할 더미 유저

    @Test
    @Transactional
    public void testBatchSizePerformance() {
        Long studentId = 1L;
        Long lectureId = 1L;
        Lecture lecture = lectureRepository.findById(lectureId).orElseThrow();
        Student student = studentRepository.findById(studentId).orElseThrow();

        int[] batchSizes = {100,500,600,700,1000}; //배치 크기 테스트

        for (int batchSize : batchSizes) {
            long startTime = System.currentTimeMillis();

            for (int i = 0; i < TOTAL_DUMMY_USERS; i++) {
                DummyUser dummyUser = new DummyUser(student, lecture);
                entityManager.persist(dummyUser);

                if (i % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }

            // 마지막 flush
            entityManager.flush();
            entityManager.clear();

            long endTime = System.currentTimeMillis();
            double executionTimeInSeconds = (endTime - startTime) / 1000.0; // 초 변환
            System.out.printf("Batch Size: %d → Execution Time: %.3f sec%n", batchSize, executionTimeInSeconds);
        }
    }
    @Test
    @Transactional
    @Commit
    public void SaveDummyUsers(){
        Long studentId = 1L;
        Long lectureId = 1L;
        Long competition=3L;
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을수 없습니다."));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("강의를 찾을수 없습니다."));

        //생성할 더미 학생 수 계산
        int count = (int) Math.round(competition * lecture.getTotalSeats()); // 경쟁률 * 정원 = 더미 학생
        long startTime = System.currentTimeMillis();

        log.info("DummyUser 생성 시작 - 총 {}명", count);
        //saveByPersist(student,lecture,count);
        //EntityBatchInsert(student.getId(), lecture.getId(), count);
        dummyUserRepository.bulkInsert(studentId,lectureId,count);
        long endTime = System.currentTimeMillis(); // 종료 시간
        double executionTimeInSeconds = (endTime - startTime) / 1000.0; // 초 변환
        //log.info("개별 쿼리 DummyUser 생성 완료 - 실행 시간: {}초", String.format("%.3f", executionTimeInSeconds));
        //log.info("JBCD DummyUser 생성 완료 - 실행 시간: {}초", String.format("%.3f", executionTimeInSeconds));
        log.info("DummyUser 벌크 생성 완료 - 실행 시간: {}초",String.format("%.3f", executionTimeInSeconds));
    }

    public void saveByPersist(Student student,Lecture lecture,int count){
        ArrayList<DummyUser> list = new ArrayList<>();
        for (int i=0;i<count;i++){
            DummyUser dummyUser = new DummyUser(student, lecture);
            list.add(dummyUser);
        }
        dummyUserRepository.saveAll(list);
    }

    // JDBC Batch insert
    public void EntityBatchInsert(Long studentId, Long lectureId, int count) {
        String sql = "INSERT INTO dummy_user (id, student_id, lecture_id) VALUES (nextval('dummy_user_seq'), ?, ?)";

        List<Object[]> batchArgs = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            batchArgs.add(new Object[]{studentId, lectureId});
        }

        jdbcTemplate.batchUpdate(sql, batchArgs);
        log.info("{}개의 DummyUser Batch Insert 완료", count);
    }
}