package triko.code_executioner.services.interfaces;

import org.springframework.http.codec.multipart.FilePart;

import reactor.core.publisher.Mono;

public interface FileSystemServiceInterface {
	Mono<String> saveExampleTestCaseImage(FilePart file);
}
