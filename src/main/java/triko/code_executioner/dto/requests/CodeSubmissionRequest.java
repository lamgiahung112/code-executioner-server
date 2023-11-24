package triko.code_executioner.dto.requests;

public record CodeSubmissionRequest(
	String userId,
	String problemId,
	String code
) {

}
