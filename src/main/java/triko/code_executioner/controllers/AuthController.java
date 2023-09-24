package triko.code_executioner.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import triko.code_executioner.configs.JwtUtils;
import triko.code_executioner.dto.requests.SignInRequest;
import triko.code_executioner.dto.requests.SignUpRequest;
import triko.code_executioner.dto.responses.ApiResponse;
import triko.code_executioner.dto.responses.SignInResult;
import triko.code_executioner.dto.responses.VerifyUserResult;
import triko.code_executioner.models.DUser;
import triko.code_executioner.services.interfaces.UserServiceInterface;
import triko.code_executioner.utilities.IsAuthenticated;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/auth")
public class AuthController {
	private final UserServiceInterface userService;
	private final JwtUtils jwtUtils;
	private final BCryptPasswordEncoder passwordEncoder;

	@PostMapping("/signup")
	public Mono<ResponseEntity<ApiResponse<DUser>>> signupUser(@RequestBody SignUpRequest signupData) {
		ApiResponse<DUser> response = new ApiResponse<>();

		return userService.save(DUser.fromSignupRequest(signupData)).flatMap(savedUser -> {
			return Mono.just(ResponseEntity.ok(response.withPayload(savedUser)));
		}).onErrorResume(error -> {
			// Handle any errors that occur during the saving process
			return Mono.just(ResponseEntity.badRequest().body(response.error("Error creating user!")));
		});
	}

	@PostMapping("/signin")
	public Mono<ResponseEntity<ApiResponse<SignInResult>>> signinUser(@RequestBody SignInRequest signInData) {
		ApiResponse<SignInResult> response = new ApiResponse<>();

		return userService.findByUsername(signInData.username())
				.filter(user -> passwordEncoder.matches(signInData.password(), user.password())).flatMap(user -> {
					SignInResult result = new SignInResult(jwtUtils.generateToken(user));
					return Mono.just(ResponseEntity.ok(response.withPayload(result)));
				}).switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()));
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/verify")
	public Mono<ResponseEntity<ApiResponse<VerifyUserResult>>> verifyUser(
			@RequestHeader(name = HttpHeaders.AUTHORIZATION) String bearerToken) {
		ApiResponse<VerifyUserResult> response = new ApiResponse<>();
		
		// Remove Bearer prefix
		String token = bearerToken.substring(7);

		Claims claims = jwtUtils.getAllClaimsFromToken(token);

		String username = claims.getSubject();
		List<String> roles = claims.get("roles", ArrayList.class);
		String name = claims.get("name", String.class);
		String id = claims.get("id", String.class);
		long exp = claims.getExpiration().getTime();
		return Mono.just(
				ResponseEntity.ok().body(response.withPayload(new VerifyUserResult(username, roles, name, id, exp))));
	}
}
