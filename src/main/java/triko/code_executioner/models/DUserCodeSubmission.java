package triko.code_executioner.models;

import java.util.List;

import org.springframework.data.annotation.Id;

import triko.code_executioner.models.enums.CodeLanguage;
import triko.code_executioner.models.enums.SubmissionStatus;

public record DUserCodeSubmission(
		@Id String id,
		String userId,
		String problemId,
		String code,
		CodeLanguage language,
		List<SubmissionStatus> result,
		SubmissionStatus status,
		long createdAt
) {

}
