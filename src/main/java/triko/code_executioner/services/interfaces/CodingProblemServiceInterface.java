package triko.code_executioner.services.interfaces;

import java.util.List;

import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CodingProblemFilterRequest;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.dto.responses.CodingProblemBasicInfoResponse;
import triko.code_executioner.models.DCodingProblem;

public interface CodingProblemServiceInterface extends DataServiceInterface<DCodingProblem> {
	Mono<DCodingProblem> createNewCodingProblem(CreateCodingProblemRequest request);
	Mono<List<CodingProblemBasicInfoResponse>> getProblemsByFilter(CodingProblemFilterRequest filterRequest);
}
