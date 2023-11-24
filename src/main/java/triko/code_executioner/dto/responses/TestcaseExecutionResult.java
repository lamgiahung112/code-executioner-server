package triko.code_executioner.dto.responses;

public record TestcaseExecutionResult(
	double time,
	String output,
	double memory,
	boolean isPassed
) {

}
