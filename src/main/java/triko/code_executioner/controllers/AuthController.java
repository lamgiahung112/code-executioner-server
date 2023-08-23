package triko.code_executioner.controllers;

import java.util.ArrayList;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import triko.code_executioner.configs.JwtUtils;
import triko.code_executioner.dto.base.TestCase;
import triko.code_executioner.dto.requests.SignInRequest;
import triko.code_executioner.dto.requests.SignUpRequest;
import triko.code_executioner.dto.responses.ApiResponse;
import triko.code_executioner.dto.responses.SignInResult;
import triko.code_executioner.models.DUser;
import triko.code_executioner.services.interfaces.UserServiceInterface;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
	private final UserServiceInterface userService;
	private final JwtUtils jwtUtils;
	private final BCryptPasswordEncoder passwordEncoder;

	@PostMapping("/signup")
	@SuppressWarnings("unchecked")
	public Mono<ResponseEntity<ApiResponse<DUser>>> signupUser(@RequestBody SignUpRequest signupData) {
		ApiResponse<DUser> response = new ApiResponse<>();

        return userService.save(DUser.fromSignupRequest(signupData))
                .flatMap(savedUser -> {
                    return Mono.just(ResponseEntity.ok(response.withPayload(savedUser)));
                })
                .onErrorResume(error -> {
                    // Handle any errors that occur during the saving process
                    return Mono.just(
                    		ResponseEntity
                    		.badRequest()
                    		.body(response.error("Error creating user!")));
                });
	}
	
	@PostMapping("/signin")
	public Mono<ResponseEntity<ApiResponse<SignInResult>>> signinUser(@RequestBody SignInRequest signInData) {
		ApiResponse<SignInResult> response = new ApiResponse<>();
		
		return userService.findByUsername(signInData.username())
				.filter(user ->  passwordEncoder.matches(signInData.password(), user.password()))
				.flatMap(user -> {
					SignInResult result = new SignInResult(jwtUtils.generateToken(user));
					return Mono.just(
							ResponseEntity.ok(response.withPayload(result))
					);
				})
				.switchIfEmpty(Mono.just(
						ResponseEntity.status(HttpStatus.UNAUTHORIZED).build())
				);
	}
	
	@GetMapping("/test")
	public Mono<ResponseEntity<String>> test() {
		
		return Mono.just(ResponseEntity.ok().body(""));
	}
}
