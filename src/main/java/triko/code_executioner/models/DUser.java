package triko.code_executioner.models;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

import triko.code_executioner.dto.requests.SignUpRequest;
import triko.code_executioner.models.enums.Role;

@Document
public record DUser(
		@Id String id,
		@Indexed(unique = true) String username,
		@JsonIgnore String password,
		String name,
		List<Role> roles,
		boolean isEnabled) {
	public static DUser fromSignupRequest(SignUpRequest request) {
		List<Role> roles = new ArrayList<>();
		roles.add(Role.ROLE_USER);
		return new DUser(null, request.username(), request.password(), request.name(), roles, true);
	}
}
