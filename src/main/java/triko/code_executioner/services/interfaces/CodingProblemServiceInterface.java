package triko.code_executioner.services.interfaces;

import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.models.DCodingProblem;

public interface CodingProblemServiceInterface extends DataServiceInterface<DCodingProblem> {
	Mono<DCodingProblem> createNewCodingProblem(CreateCodingProblemRequest request);
}
