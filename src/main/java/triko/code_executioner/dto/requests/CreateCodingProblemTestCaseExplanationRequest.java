package triko.code_executioner.dto.requests;

public record CreateCodingProblemTestCaseExplanationRequest(
		String exampleTest,
		String exampleResult
		) {
}
