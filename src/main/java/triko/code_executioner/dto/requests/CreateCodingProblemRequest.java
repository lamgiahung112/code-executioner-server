package triko.code_executioner.dto.requests;

import java.util.List;

import org.springframework.http.codec.multipart.FilePart;

import triko.code_executioner.dto.base.TestCase;
import triko.code_executioner.models.enums.ProblemDifficulty;
import triko.code_executioner.models.enums.ProblemTag;

public record CreateCodingProblemRequest(
		String title,
		String description,
		List<String> constraints,
		ProblemDifficulty difficulty,
		String defaultCodeDefinition,
		List<ProblemTag> tags,
		List<TestCase> testcases,
		List<TestCase> exampleTestcase,
		List<String> exampleTestcaseExplanation,
		List<FilePart> exampleTestCaseImage
) {

}
