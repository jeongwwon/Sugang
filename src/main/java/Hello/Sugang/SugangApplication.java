package Hello.Sugang;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SugangApplication {

	public static void main(String[] args) {
		SpringApplication.run(SugangApplication.class, args);
	}

}
