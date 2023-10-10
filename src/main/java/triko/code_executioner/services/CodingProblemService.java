package triko.code_executioner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.models.DExampleTestCaseExplanation;
import triko.code_executioner.repositories.CodingProblemRepositoryInterface;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;
import triko.code_executioner.services.interfaces.FileSystemServiceInterface;
import triko.code_executioner.utilities.CodeExecutorQueueService;

@Service
@AllArgsConstructor
public class CodingProblemService implements CodingProblemServiceInterface {
	private final CodingProblemRepositoryInterface codingProblemRepository;
	private final CodeExecutorQueueService codeExecutorQueueService;
	private final FileSystemServiceInterface fileSystemService;

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

	@Override
	public Mono<DCodingProblem> createNewCodingProblem(CreateCodingProblemRequest request) {
		DCodingProblem problem = new DCodingProblem();

		problem.setTitle(request.title());
		problem.setDescription(request.description());
		problem.setConstraints(request.constraints());
		problem.setDifficulty(request.difficulty());
		problem.setTags(request.tags());
		problem.setAcceptanceCount(0);
		problem.setDislikeCount(0);
		problem.setLikeCount(0);
		problem.setPending(true);
		problem.setSubmissionCount(0);

		List<DExampleTestCaseExplanation> testcaseExplanationList = new ArrayList<>();
		for (int i = 0; i < request.exampleTestcaseExplanation().size(); i++) {
			FilePart currentTestCaseImg = request.exampleTestCaseImage().get(i);
			String currentTestCaseTest = request.exampleTestcaseExplanation().get(i).getTest();
			String currentTestCaseExpected = request.exampleTestcaseExplanation().get(i).getExpected();

			fileSystemService.saveFile(currentTestCaseImg).flatMap(fileName -> {
				testcaseExplanationList
						.add(new DExampleTestCaseExplanation(fileName, currentTestCaseTest, currentTestCaseExpected));
				return Mono.empty();
			});
		}
		
		problem.setExampleTestCaseExplanations(testcaseExplanationList);

		return save(problem).flatMap(createdProblem -> {
			codeExecutorQueueService.sendRequestMessageToQueue(
					new SaveTestCaseFileRequest(createdProblem.getId(), request.testcases()));
			return Mono.just(createdProblem);
		});
	}

}
