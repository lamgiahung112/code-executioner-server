package triko.code_executioner.services.interfaces;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public interface FileSystemServiceInterface {
	Mono<String> saveFile(FilePart file);
	void deleteFile(String filePath);
}
