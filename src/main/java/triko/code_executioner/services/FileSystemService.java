package triko.code_executioner.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import triko.code_executioner.services.interfaces.FileSystemServiceInterface;

@Service
public class FileSystemService implements FileSystemServiceInterface {
	@Value("${spring.web.resources.static-locations}")
	private String STATIC_DIR;
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	@Override
	public Mono<String> saveExampleTestCaseImage(FilePart file) {
		try {
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") + file.filename();

            // Define the path where you want to save the file
            Path filePath = Path.of(resourceLoader.getResource(STATIC_DIR).getFile().getAbsolutePath(), fileName);

            // Create the directory if it doesn't exist
            Files.createDirectories(filePath.getParent());

            // Transfer the file to the specified path
            return file.transferTo(filePath.toFile()).then(Mono.just("/static/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        
	}

}
