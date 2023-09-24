package triko.code_executioner.dto.responses;

import java.util.List;

import triko.code_executioner.models.enums.Role;

public record VerifyUserResult(String username, List<String> role, String name, String id, long exp) {

}
