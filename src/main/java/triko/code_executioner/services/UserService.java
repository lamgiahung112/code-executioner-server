package triko.code_executioner.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import triko.code_executioner.models.DUser;
import triko.code_executioner.repositories.UserRepositoryInterface;
import triko.code_executioner.services.interfaces.UserServiceInterface;

@Service
public class UserService implements UserServiceInterface {
	private final UserRepositoryInterface userRepository;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	public UserService(UserRepositoryInterface userRepository, BCryptPasswordEncoder pwEncoder) {
		this.userRepository = userRepository;
		this.bCryptPasswordEncoder = pwEncoder;
	}

	@Override
	public Mono<DUser> findById(String id) {
		return userRepository.findById(id);
	}

	@Override
	public Flux<DUser> findAll() {
		return userRepository.findAll();
	}

	@Override
	public Mono<DUser> save(DUser entry) {
		DUser userWithHashedPassword 
			= new DUser(
				entry.id(), 
				entry.username(), 
				bCryptPasswordEncoder.encode(entry.password()), 
				entry.name(),
				entry.roles(),
				entry.isEnabled()
		);
		return userRepository.save(userWithHashedPassword);
	}
	@Override
	public void remove(DUser entry) {
		userRepository.delete(entry);
	}

	@Override
	public Mono<DUser> findByUsername(String username) {
		return userRepository.findByUsername(username);
	}
}
