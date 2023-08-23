package triko.code_executioner.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.dto.responses.ApiResponse;
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;
import triko.code_executioner.utilities.CodeExecutorQueueService;

@RestController
@RequestMapping("/problem")
@AllArgsConstructor
public class CodingProblemController {
	private final CodingProblemServiceInterface codingProblemService;
	private final CodeExecutorQueueService codeExecutorQueueService;
	
	@PostMapping
	public Mono<ResponseEntity<ApiResponse<DCodingProblem>>> createNewCodingProblem(@RequestBody CreateCodingProblemRequest request) {
		ApiResponse<DCodingProblem> res = new ApiResponse<>();
		
		DCodingProblem newProblem = DCodingProblem.fromCreateProblemRequest(request);
		
		return codingProblemService
				.save(newProblem)
				.flatMap(savedProblem -> {
					codeExecutorQueueService.sendSaveTestCaseMessageToQueue(
							new SaveTestCaseFileRequest(savedProblem.id(), request.testcases())
					);
					return Mono.just(
							ResponseEntity
								.ok()
								.body(res.withPayload(savedProblem))
					);
				});
	}
}
