package triko.code_executioner.services.interfaces;

import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import reactor.core.publisher.Flux;
import triko.code_executioner.dto.requests.CodingProblemFilterRequest;
import triko.code_executioner.dto.responses.CodingProblemBasicInfoResponse;
import triko.code_executioner.models.DCodingProblem;

public interface CodingProblemServiceInterface extends DataServiceInterface<DCodingProblem> {
	Mono<DCodingProblem> createNewCodingProblem(CreateCodingProblemRequest request);
	Flux<CodingProblemBasicInfoResponse> getProblemsByFilter(CodingProblemFilterRequest filterRequest); 
}
