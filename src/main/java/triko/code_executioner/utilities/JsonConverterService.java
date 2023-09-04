package triko.code_executioner.utilities;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
public class JsonConverterService {
	public <T> T parseToObject(String json, Class<T> type) {
		try {
			return new ObjectMapper().readValue(json, type);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public String convert(Object object) {
		return new Gson().toJson(object);
	}
}
