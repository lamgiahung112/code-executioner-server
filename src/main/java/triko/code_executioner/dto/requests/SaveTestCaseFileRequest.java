package triko.code_executioner.dto.requests;

import java.util.List;

import triko.code_executioner.dto.base.TestCase;

public record SaveTestCaseFileRequest(
		String problemId,
		List<TestCase> testcases
) {

}
