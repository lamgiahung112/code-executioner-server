package triko.code_executioner.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import triko.code_executioner.models.DCodingProblem;

@Repository
public interface CodingProblemRepositoryInterface extends ReactiveMongoRepository<DCodingProblem, String> {

}
