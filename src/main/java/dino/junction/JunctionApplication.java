package dino.junction;

import dino.junction.common.aop.LogAop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

//@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@SpringBootApplication()
@EnableJpaAuditing
@Import(LogAop.class)
public class JunctionApplication {

	@PostConstruct
	void setTImeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}


	public static void main(String[] args) {
		SpringApplication.run(JunctionApplication.class, args);
	}

}
