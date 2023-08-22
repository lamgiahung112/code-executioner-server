package triko.code_executioner.dto.responses;

import java.util.Optional;

public record TestResult(Optional<String> test, Optional<String> expected, Optional<String> result, boolean isPassed) {

}
