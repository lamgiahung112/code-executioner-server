package triko.code_executioner.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import triko.code_executioner.models.enums.ProblemDifficulty;

@AllArgsConstructor
@Builder
@Data
public class CodingProblemBasicInfoResponse {
	private String id;
	private String title;
	private ProblemDifficulty difficulty;
	private int likeCount;
	private int dislikeCount;
	private int submissionCount;
	private int acceptanceCount;
}
