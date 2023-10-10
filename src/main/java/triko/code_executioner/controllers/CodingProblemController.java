package triko.code_executioner.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import triko.code_executioner.configs.JwtUtils;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.dto.responses.ApiResponse;
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.models.enums.ProblemDifficulty;
import triko.code_executioner.models.enums.ProblemTag;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;
import triko.code_executioner.services.interfaces.FileSystemServiceInterface;
import triko.code_executioner.utilities.CodeExecutorQueueService;
import triko.code_executioner.utilities.IsAuthenticated;
import triko.code_executioner.utilities.IsProblemSetter;

@RestController
@CrossOrigin
@RequestMapping("/problem")
@AllArgsConstructor
public class CodingProblemController {
	private final Logger logger = LoggerFactory.getLogger(CodingProblemController.class);
	private final CodingProblemServiceInterface codingProblemService;
	private final CodeExecutorQueueService codeExecutorQueueService;
	private final JwtUtils jwtUtils;
	private final FileSystemServiceInterface fileSystemService;

	@PostMapping
	@IsProblemSetter
	public Mono<ResponseEntity<ApiResponse<DCodingProblem>>> createNewCodingProblem(
			@ModelAttribute @Validated CreateCodingProblemRequest request) {
		ApiResponse<DCodingProblem> res = new ApiResponse<>();

		DCodingProblem newProblem = DCodingProblem.fromCreateProblemRequest(request);

		return codingProblemService.save(newProblem).flatMap(savedProblem -> {
//			codeExecutorQueueService
//					.sendRequestMessageToQueue(new SaveTestCaseFileRequest(savedProblem.getId(), request.testcases()));
			return Mono.just(ResponseEntity.ok().body(res.withPayload(savedProblem)));
		});
	}

	@GetMapping
	@IsAuthenticated
	public Mono<ResponseEntity<ApiResponse<List<DCodingProblem>>>> getProblems(
			@Header(name = HttpHeaders.AUTHORIZATION) String accessToken,
			@PathVariable List<ProblemDifficulty> difficulty, @PathVariable Boolean isAccepted,
			@PathVariable List<ProblemTag> tags, @PathVariable int page, @PathVariable int itemsPerPage) {
		ApiResponse<List<DCodingProblem>> res = new ApiResponse<>();

		String userId = jwtUtils.getIdFromToken(accessToken);

		return Mono.just(ResponseEntity.ok().body(res));
	}

}
