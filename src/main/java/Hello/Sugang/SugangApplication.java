package Hello.Sugang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SugangApplication {

	public static void main(String[] args) {
		System.out.println("권한 설정 완료");
		SpringApplication.run(SugangApplication.class, args);
	}

}
