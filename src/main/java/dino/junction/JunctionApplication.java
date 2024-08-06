package dino.junction;

import dino.junction.common.logger.aop.LogAop;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Import;

//@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@SpringBootApplication()
@Import(LogAop.class)
public class JunctionApplication {

	public static void main(String[] args) {
		SpringApplication.run(JunctionApplication.class, args);
	}

}
