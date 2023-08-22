package triko.code_executioner.models;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public record DExampleTestCaseExplanation(
		@Id String id,
		List<String> imgPaths,
		String exampleTest,
		String exampleResult
) {

}
