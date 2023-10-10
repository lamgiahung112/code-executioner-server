package triko.code_executioner.dto.requests;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import triko.code_executioner.dto.base.TestCase;
import triko.code_executioner.models.DExampleTestCaseExplanation;
import triko.code_executioner.models.enums.ProblemDifficulty;
import triko.code_executioner.models.enums.ProblemTag;

public record CreateCodingProblemRequest(
		String title,
		String description,
		List<String> constraints,
		ProblemDifficulty difficulty,
		List<ProblemTag> tags,
		List<TestCase> testcases
//		List<CreateCodingProblemTestCaseExplanationRequest> exampleTestcaseExplanation
) {

}
