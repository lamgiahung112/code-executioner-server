package triko.code_executioner.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import triko.code_executioner.utilities.converters.StringToTestCaseConverter;

@Configuration
public class WebConfig implements WebFluxConfigurer {
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new StringToTestCaseConverter());
		WebFluxConfigurer.super.addFormatters(registry);
	}
}
