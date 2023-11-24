package triko.code_executioner.controllers;

import java.util.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CodeExecutionRequest;
import triko.code_executioner.dto.requests.CodeSubmissionRequest;
import triko.code_executioner.dto.requests.CodingProblemFilterRequest;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.dto.responses.ApiResponse;
import triko.code_executioner.dto.responses.CodingProblemBasicInfoResponse;
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.models.DUserCodeSubmission;
import triko.code_executioner.models.enums.SubmissionStatus;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;
import triko.code_executioner.services.interfaces.UserCodeSubmissionServiceInterface;
import triko.code_executioner.utilities.CodeExecutorQueueService;
import triko.code_executioner.utilities.IsAuthenticated;

@RestController
@CrossOrigin
@RequestMapping("/problem")
@AllArgsConstructor
public class CodingProblemController {
	private final CodingProblemServiceInterface codingProblemService;
	private final CodeExecutorQueueService codeExecutorQueueService;
	private final UserCodeSubmissionServiceInterface userCodeSubmissionService;
	
	@PostMapping
	@IsAuthenticated
	public Mono<ResponseEntity<ApiResponse<DCodingProblem>>> createNewCodingProblem(Authentication authentication,
			@ModelAttribute @Validated CreateCodingProblemRequest request) {
		ApiResponse<DCodingProblem> res = new ApiResponse<>();
		return codingProblemService.createNewCodingProblem(request).flatMap(createdProblem -> {
			codeExecutorQueueService.sendRequestMessageToQueue(
					new SaveTestCaseFileRequest(createdProblem.getId(), request.testcases()));
			return Mono.just(createdProblem);
		}).flatMap(savedProblem -> {
			return Mono.just(ResponseEntity.ok().body(res.withPayload(savedProblem)));
		});
	}

	@GetMapping
	@IsAuthenticated
	public Mono<ResponseEntity<ApiResponse<List<CodingProblemBasicInfoResponse>>>> getProblemsByFilter(
	        @ModelAttribute CodingProblemFilterRequest filterRequest) {
		ApiResponse<List<CodingProblemBasicInfoResponse>> res = new ApiResponse<>();
		return codingProblemService.getProblemsByFilter(filterRequest)
				.flatMap(problemList -> Mono.just(ResponseEntity.ok().body(res.withPayload(problemList))));
	}

	@GetMapping("/getById")
	@IsAuthenticated
	public Mono<ResponseEntity<ApiResponse<DCodingProblem>>> getById(@RequestParam(value = "id", required = true) String problemId) {
		ApiResponse<DCodingProblem> res = new ApiResponse<>();

		return codingProblemService
				.findById(problemId)
				.flatMap(
						foundProblem -> Mono.just(ResponseEntity.ok().body(res.withPayload(foundProblem)
				)
			)
		);
	}
	
	@PostMapping("/submit")
	@IsAuthenticated
	public Mono<ResponseEntity<ApiResponse<DUserCodeSubmission>>> submitSolution(@RequestBody @Validated CodeSubmissionRequest request) {
		ApiResponse<DUserCodeSubmission> res = new ApiResponse<>();
		
		DUserCodeSubmission submission = DUserCodeSubmission.builder()
				.code(request.code())
				.userId(request.userId())
				.problemId(request.problemId())
				.status(SubmissionStatus.PENDING)
				.submittedAt(new Date().getTime())
				.build();
		
		return userCodeSubmissionService.save(submission)
				.flatMap(savedSubmission -> {
					CodeExecutionRequest execRequest = new CodeExecutionRequest(savedSubmission.getId(), savedSubmission.getProblemId(), savedSubmission.getCode());
					codeExecutorQueueService.sendRequestMessageToQueue(execRequest);
					return Mono.just(ResponseEntity.ok().body(res.withPayload(savedSubmission))); 
				});
	}
}
