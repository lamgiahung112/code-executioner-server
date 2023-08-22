package triko.code_executioner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class})
public class CodeExecutionerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodeExecutionerApplication.class, args);
	}

	
}
