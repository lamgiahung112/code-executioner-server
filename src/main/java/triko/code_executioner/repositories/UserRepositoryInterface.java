package triko.code_executioner.repositories;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import reactor.core.publisher.Mono;
import triko.code_executioner.models.DUser;

@Repository
public interface UserRepositoryInterface extends ReactiveMongoRepository<DUser, String> {
	Mono<DUser> findByUsername(String username);
}
