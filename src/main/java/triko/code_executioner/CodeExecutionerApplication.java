package triko.code_executioner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

import triko.code_executioner.utilities.AppInitializer;

@SpringBootApplication(exclude = {ReactiveUserDetailsServiceAutoConfiguration.class})
public class CodeExecutionerApplication {
	public static void main(String[] args) {
		AppInitializer appInitializer = new AppInitializer();
		appInitializer.init();
		SpringApplication.run(CodeExecutionerApplication.class, args);
	}
}
