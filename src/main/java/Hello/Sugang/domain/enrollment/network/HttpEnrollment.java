package Hello.Sugang.domain.enrollment.network;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class HttpEnrollment {

    private static final RestTemplate restTemplate=new RestTemplate();

    public static void request(Long dummyUserId,Long lectureId) {
        String url = "http://localhost:8080/enrollment/dummy/" + dummyUserId+"/"+lectureId;
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);
        } catch (Exception e) {
            log.error("등록에 실패한 Student ID: {}", dummyUserId, e);
        }
    }
}
