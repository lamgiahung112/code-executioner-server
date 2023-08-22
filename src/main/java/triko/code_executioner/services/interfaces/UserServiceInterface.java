package triko.code_executioner.services.interfaces;

import reactor.core.publisher.Mono;
import triko.code_executioner.models.DUser;

public interface UserServiceInterface extends DataServiceInterface<DUser> {
	Mono<DUser> findByUsername(String username);
}
