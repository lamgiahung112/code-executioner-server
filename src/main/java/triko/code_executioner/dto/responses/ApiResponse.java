package triko.code_executioner.dto.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class ApiResponse<T> {
	private T payload;
	private String error;
	
	public ApiResponse() {
	}
	
	public ApiResponse<T> error(String error) {
		this.error = error;
		return this;
	}
	
	public ApiResponse<T> withPayload(T payload) {
		this.payload = payload;
		return this;
	}
}
