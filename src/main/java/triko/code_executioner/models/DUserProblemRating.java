package triko.code_executioner.models;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
	@CompoundIndex(
			name = "userid-and-problemid-as-unique",
			def = "{'userId' : 1, 'problemId': 1}",
			unique = true
	)
})
public record DUserProblemRating(
		String id,
		String userId,
		String problemId,
		boolean isLike
) {

}
