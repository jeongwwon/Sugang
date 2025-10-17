package Hello.Sugang.domain.enrollment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service("kafka")
@RequiredArgsConstructor
public class KafkaEnrollmentService implements Enrollment {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "enrollment_v2";

    @Override
    public ResponseEntity<String> register(Long studentId, Long lectureId) {
        String message = studentId + ":" + lectureId;

        // CompletableFuture 기반 비동기 처리
        kafkaTemplate.send(TOPIC,lectureId.toString(),message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        System.out.println("Kafka 전송 성공: " + message);
                    } else {
                        System.err.println("Kafka 전송 실패: " + message + ", 에러=" + ex.getMessage());
                    }
                });

        // 즉시 응답 반환 (Kafka 적재만 보장)
        return ResponseEntity
                .status(HttpStatus.ACCEPTED) // 202 Accepted
                .body("수강신청 요청이 접수되었습니다. studentId="
                        + studentId + ", lectureId=" + lectureId);
    }
}
