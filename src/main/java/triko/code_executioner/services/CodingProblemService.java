package triko.code_executioner.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triko.code_executioner.dto.base.TestCase;
import triko.code_executioner.dto.requests.CodingProblemFilterRequest;
import triko.code_executioner.dto.requests.CreateCodingProblemRequest;
import triko.code_executioner.dto.responses.CodingProblemBasicInfoResponse;
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.models.DExampleTestCaseExplanation;
import triko.code_executioner.repositories.CodingProblemRepositoryInterface;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;

@Service
@AllArgsConstructor
@Slf4j
public class CodingProblemService implements CodingProblemServiceInterface {
	private final CodingProblemRepositoryInterface codingProblemRepository;
	private final FileSystemService fileSystemService;
	private final ReactiveMongoTemplate mongoTemplate;

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
	public Mono<List<CodingProblemBasicInfoResponse>> getProblemsByFilter(CodingProblemFilterRequest filterRequest) {
		PageRequest pageRequest = PageRequest.of(filterRequest.getPage(), filterRequest.getPageSize());
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
				Aggregation.match(matchesSearch),
				Aggregation.skip(pageRequest.getOffset()),
				Aggregation.limit(pageRequest.getPageSize())
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
                }).collectList();
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

		// Process exampleTestcaseExplanation and exampleTestCaseImage in a reactive way
	    Flux<DExampleTestCaseExplanation> processedTestcases = Flux.range(0, request.exampleTestcaseExplanation().size())
	            .flatMap(i -> {
	                TestCase explanation = request.exampleTestcaseExplanation().get(i);
	                FilePart currentTestCaseImg = request.exampleTestCaseImage().get(i);

	                return fileSystemService.saveFile(currentTestCaseImg)
	                        .map(fileName -> new DExampleTestCaseExplanation(fileName, explanation.getTest(), explanation.getExpected()));
	            });

	    // Set the exampleTestCaseExplanations property
	    return processedTestcases.collectList()
	            .map(testcaseExplanationList -> {
	                problem.setExampleTestCaseExplanations(testcaseExplanationList);
	                return problem;
	            })
	            .flatMap(this::save);
	}

}
