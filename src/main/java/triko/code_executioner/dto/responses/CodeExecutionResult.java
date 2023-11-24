package triko.code_executioner.dto.responses;

import java.util.List;

public record CodeExecutionResult(
	List<TestcaseExecutionResult> result,
	String submissionId,
	long completedAt
) {

}
