package triko.code_executioner.services.interfaces;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface DataServiceInterface<T> {
	Mono<T> findById(String id);
	Flux<T> findAll();
	Mono<T> save(T entry);
	void remove(T entry);
}
