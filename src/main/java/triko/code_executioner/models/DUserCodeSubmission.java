package triko.code_executioner.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import triko.code_executioner.models.enums.SubmissionStatus;

@Data
@Document
@Builder
@AllArgsConstructor
public class DUserCodeSubmission {
	@Id 
	private String id;
	private String userId;
	private String problemId;
	private String code;
	private SubmissionStatus status;
	private long submittedAt;
	private long completedAt;
}
