package triko.code_executioner.dto.requests;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import triko.code_executioner.models.enums.ProblemDifficulty;
import triko.code_executioner.models.enums.ProblemTag;
import triko.code_executioner.models.enums.UserProblemProgress;

@AllArgsConstructor
@Data
public class CodingProblemFilterRequest {
	private ProblemDifficulty difficulty;
	private UserProblemProgress status;
	private List<ProblemTag> tags;
	private String search;
}
