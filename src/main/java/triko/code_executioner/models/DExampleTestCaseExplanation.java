package triko.code_executioner.models;

import lombok.Getter;
import lombok.Setter;
import triko.code_executioner.dto.base.TestCase;

public class DExampleTestCaseExplanation extends TestCase {
	@Getter
	@Setter
	private String imgPath;
	
	public DExampleTestCaseExplanation(String imgPath, String test, String expected) {
		super(test, expected);
		this.imgPath = imgPath;
	}
}
