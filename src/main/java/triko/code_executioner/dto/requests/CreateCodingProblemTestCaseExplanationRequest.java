package triko.code_executioner.dto.requests;

import org.springframework.http.codec.multipart.FilePart;

public record CreateCodingProblemTestCaseExplanationRequest(
		FilePart img,
		String exampleTest,
		String exampleResult
		) {
}
