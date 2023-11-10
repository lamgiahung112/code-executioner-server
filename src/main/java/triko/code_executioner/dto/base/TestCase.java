package triko.code_executioner.dto.base;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TestCase {
	private String test;
	private String expected;
}
