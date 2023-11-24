package triko.code_executioner.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

public class AppInitializer {
	private final Logger logger = LoggerFactory.getLogger(AppInitializer.class);
	private final String STATIC_DIR = "classpath:/static";

	private final ResourceLoader resourceLoader = new DefaultResourceLoader();

	public void init() {
		initStaticFilePath();
	}

	private void initStaticFilePath() {
		try {
			logger.info("Initializing Static File Path");
			Files.createDirectories(Path.of(resourceLoader.getResource(STATIC_DIR).getFile().getAbsolutePath(), ""));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
