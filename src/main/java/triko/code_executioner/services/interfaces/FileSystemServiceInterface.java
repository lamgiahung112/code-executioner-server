package triko.code_executioner.services.interfaces;

import java.util.List;

import triko.code_executioner.dto.base.TestCase;

public interface FileSystemServiceInterface {
	public String saveTestCases(List<TestCase> testcases);
}
