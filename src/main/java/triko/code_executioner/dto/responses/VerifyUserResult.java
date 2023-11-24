package triko.code_executioner.dto.responses;

import java.util.List;

public record VerifyUserResult(String username, List<String> role, String name, String id, long exp) {

}
