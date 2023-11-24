package triko.code_executioner.dto.requests;

public record CodeExecutionRequest(
		String submissionId,
		String problemId,
		String code
) {
}
