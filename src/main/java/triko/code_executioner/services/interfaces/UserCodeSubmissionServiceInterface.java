package triko.code_executioner.services.interfaces;

import reactor.core.publisher.Mono;
import triko.code_executioner.models.DUserCodeSubmission;

public interface UserCodeSubmissionServiceInterface extends DataServiceInterface<DUserCodeSubmission> {
	Mono<DUserCodeSubmission> findByProblemIdAndUserId(String problemId, String userId);
}
