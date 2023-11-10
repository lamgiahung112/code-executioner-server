package triko.code_executioner.services;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CodingProblemFilterRequest;
import triko.code_executioner.dto.responses.CodingProblemBasicInfoResponse;
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.repositories.CodingProblemRepositoryInterface;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;

@Service
@AllArgsConstructor
public class CodingProblemService implements CodingProblemServiceInterface {
	private final CodingProblemRepositoryInterface codingProblemRepository;
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

}
