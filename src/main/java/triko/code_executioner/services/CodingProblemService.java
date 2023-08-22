package triko.code_executioner.services;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.repositories.CodingProblemRepositoryInterface;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;

@Service
@AllArgsConstructor
public class CodingProblemService implements CodingProblemServiceInterface {
	private final CodingProblemRepositoryInterface codingProblemRepository;
	
	@Override
	public Mono<DCodingProblem> findById(String id) {
		return codingProblemRepository.findById(id);
	}

	@Override
	public Flux<DCodingProblem> findAll() {
		return codingProblemRepository.findAll();
	}

	@Override
	public Mono<DCodingProblem> save(DCodingProblem entry) {
		return codingProblemRepository.save(entry);
	}

	@Override
	public void remove(DCodingProblem entry) {
		codingProblemRepository.delete(entry);
	}

}
