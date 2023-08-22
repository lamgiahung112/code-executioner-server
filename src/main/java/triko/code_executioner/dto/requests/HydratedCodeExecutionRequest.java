package triko.code_executioner.dto.requests;

import triko.code_executioner.models.enums.CodeLanguage;

public record HydratedCodeExecutionRequest(
		String userId,
		String problemId,
		String code,
		CodeLanguage language,
		String testcasePath,
		String resultPath
) {

}
