package triko.code_executioner.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import triko.code_executioner.dto.base.TestCase;

@Data
@EqualsAndHashCode(callSuper=false)
public class DExampleTestCaseExplanation extends TestCase {
	private String explanation;
	private String imgPath;

	public DExampleTestCaseExplanation(String imgPath, String explanation, String test, String expected) {
		super(test, expected);
		this.imgPath = imgPath;
		this.explanation = explanation;
	}
}
