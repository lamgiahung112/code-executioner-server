package triko.code_executioner.dto.responses;

public record RunAgainstTestCaseResult(
		double time,
		String output,
		double memory,
		boolean isPassed
) {
}
