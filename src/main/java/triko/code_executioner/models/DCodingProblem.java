package triko.code_executioner.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.models.enums.ProblemDifficulty;
import triko.code_executioner.models.enums.ProblemTag;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DCodingProblem {
	@Id
	private String id;
	private String title;
	private String description;
	private List<String> constraints;
	private ProblemDifficulty difficulty;
	private int likeCount;
	private int dislikeCount;
	private int submissionCount;
	private int acceptanceCount;
	private List<ProblemTag> tags;
	private List<DExampleTestCaseExplanation> exampleTestCaseExplanations;
	private boolean isPending;
}
