package triko.code_executioner.dto.requests;

import java.util.List;

import triko.code_executioner.dto.base.TestCase;
import triko.code_executioner.models.enums.ProblemDifficulty;
import triko.code_executioner.models.enums.ProblemTag;

public record CreateCodingProblemRequest(
		String title,
		String description,
		List<String> constraints,
		ProblemDifficulty difficulty,
		List<ProblemTag> tags,
		List<TestCase> testcases
) {

}
