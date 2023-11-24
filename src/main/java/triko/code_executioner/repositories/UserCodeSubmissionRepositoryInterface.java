package triko.code_executioner.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import triko.code_executioner.models.DUserCodeSubmission;

@Repository
public interface UserCodeSubmissionRepositoryInterface extends ReactiveMongoRepository<DUserCodeSubmission, String>{
	Mono<DUserCodeSubmission> findByProblemIdAndUserId(String problemId, String userId);
}
