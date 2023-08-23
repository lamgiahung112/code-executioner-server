package triko.code_executioner.configs;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {
	private final JwtUtils jwtUtils;
	
	@Override
	public Mono<Authentication> authenticate(Authentication authentication) {
		String authToken = authentication.getCredentials().toString();
		
		return Mono.just(jwtUtils.validateToken(authToken))
				.filter(valid -> valid)
				.switchIfEmpty(Mono.empty())
				.map(valid -> {
					ArrayList<String> roleMap = jwtUtils.getRolesFromToken(authToken);
					String username = jwtUtils.getUsernameFromToken(authToken);
					return new UsernamePasswordAuthenticationToken(
							username, 
							null, 
							roleMap.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
					);
				});
	}
}
