package triko.code_executioner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CodingProblemFilterRequest;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.dto.responses.CodingProblemBasicInfoResponse;
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.models.DExampleTestCaseExplanation;
import triko.code_executioner.repositories.CodingProblemRepositoryInterface;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;
import triko.code_executioner.utilities.CodeExecutorQueueService;

@Service
@AllArgsConstructor
public class CodingProblemService implements CodingProblemServiceInterface {
	private final CodingProblemRepositoryInterface codingProblemRepository;
	private final FileSystemService fileSystemService;
	private final ReactiveMongoTemplate mongoTemplate;
	private final CodeExecutorQueueService codeExecutorQueueService;

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
	public Flux<CodingProblemBasicInfoResponse> getProblemsByFilter(CodingProblemFilterRequest filterRequest) {
		Criteria isNotPending = Criteria.where("isPending").is(false);
		Criteria hasTheSameDifficulty = 
				filterRequest.getDifficulty() != null 
					? Criteria.where("difficulty").is(filterRequest.getDifficulty()) 
					: new Criteria();
		
		Criteria hasTheSameTags =
				filterRequest.getTags() != null
					? Criteria.where("tags").in(filterRequest.getTags())
					: new Criteria();
		Criteria matchesSearch = 
				filterRequest.getSearch() != null
					? Criteria.where("title").regex(filterRequest.getSearch())
					: new Criteria();
		
		Aggregation agg = Aggregation.newAggregation(
				Aggregation.match(isNotPending),
				Aggregation.match(hasTheSameDifficulty),
				Aggregation.match(hasTheSameTags),
				Aggregation.match(matchesSearch)
		);
	
        return mongoTemplate
                .aggregate(agg, "dCodingProblem", DCodingProblem.class)
                .map(problem -> {
                	return CodingProblemBasicInfoResponse.builder()
                			.id(problem.getId())
                			.title(problem.getTitle())
                			.difficulty(problem.getDifficulty())
                			.likeCount(problem.getLikeCount())
                			.dislikeCount(problem.getDislikeCount())
                			.submissionCount(problem.getSubmissionCount())
                			.acceptanceCount(problem.getAcceptanceCount())
                			.build();
                });
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
