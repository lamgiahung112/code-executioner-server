package triko.code_executioner.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.models.enums.ProblemDifficulty;
import triko.code_executioner.models.enums.ProblemTag;

@Document
public record DCodingProblem(
		@Id String id,
		String title,
		String description,
		List<String> constraints,
		ProblemDifficulty difficulty,
		int likeCount,
		int dislikeCount,
		int submissionCount,
		int acceptanceCount,
		List<ProblemTag> tags,
		@JsonIgnore String testcasePath
) {
	public static DCodingProblem fromCreateProblemRequest(CreateCodingProblemRequest request) {
		return new DCodingProblem(
				null, 
				request.title(), 
				request.description(), 
				request.constraints(), 
				request.difficulty(), 
				0, 0, 0, 0, 
				request.tags(),
				null
		);
	}
	
	public DCodingProblem withTestCasePath(String path) {
		return new DCodingProblem(
				this.id, 
				this.title,
				this.description, 
				this.constraints, 
				this.difficulty, 
				this.likeCount, this.dislikeCount, this.submissionCount, this.acceptanceCount, 
				this.tags, path);
	}
}
