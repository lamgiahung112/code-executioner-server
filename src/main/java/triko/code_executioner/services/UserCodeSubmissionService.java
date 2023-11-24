package triko.code_executioner.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triko.code_executioner.models.DUserCodeSubmission;
import triko.code_executioner.repositories.UserCodeSubmissionRepositoryInterface;
import triko.code_executioner.services.interfaces.UserCodeSubmissionServiceInterface;

@Service
public class UserCodeSubmissionService implements UserCodeSubmissionServiceInterface {
	@Autowired
	private UserCodeSubmissionRepositoryInterface userCodeSubmissionRepository;

	@Override
	public Mono<DUserCodeSubmission> findById(String id) {
		return userCodeSubmissionRepository.findById(id);
	}

	@Override
	public Flux<DUserCodeSubmission> findAll() {
		return userCodeSubmissionRepository.findAll();
	}

	@Override
	public Mono<DUserCodeSubmission> save(DUserCodeSubmission entry) {
		return userCodeSubmissionRepository.save(entry);
	}

	@Override
	public void remove(DUserCodeSubmission entry) {
		userCodeSubmissionRepository.delete(entry);
	}

	@Override
	public Mono<DUserCodeSubmission> findByProblemIdAndUserId(String problemId, String userId) {
		return userCodeSubmissionRepository.findByProblemIdAndUserId(problemId, userId);
	}

}
