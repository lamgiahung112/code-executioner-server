package triko.code_executioner.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.Authentication;
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
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triko.code_executioner.configs.JwtUtils;
import triko.code_executioner.dto.requests.CodingProblemFilterRequest;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.dto.responses.ApiResponse;
import triko.code_executioner.dto.responses.CodingProblemBasicInfoResponse;
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
@Slf4j
public class CodingProblemController {
	private final CodingProblemServiceInterface codingProblemService;
	private final JwtUtils jwtUtils;

	@PostMapping
	@IsProblemSetter
	public Mono<ResponseEntity<ApiResponse<DCodingProblem>>> createNewCodingProblem(
			Authentication authentication,
			@ModelAttribute @Validated CreateCodingProblemRequest request) {
		ApiResponse<DCodingProblem> res = new ApiResponse<>();
		return codingProblemService.createNewCodingProblem(request).flatMap(savedProblem -> {
			return Mono.just(ResponseEntity.ok().body(res.withPayload(savedProblem)));
		});
	}

	
	@GetMapping
	@IsAuthenticated
	public Mono<ResponseEntity<ApiResponse<Flux<CodingProblemBasicInfoResponse>>>> getProblemsByFilter(
//				@Header(name = HttpHeaders.AUTHORIZATION) String accessToken,
				@RequestParam CodingProblemFilterRequest filterRequest
			) {
		ApiResponse<Flux<CodingProblemBasicInfoResponse>> res = new ApiResponse<>();

//		String userId = jwtUtils.getIdFromToken(accessToken);

		return Mono.just(
			ResponseEntity.ok()
				.body(
					res.withPayload(codingProblemService.getProblemsByFilter(filterRequest))
				)
		);
	}

}
