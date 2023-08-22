package triko.code_executioner.utilities;

import org.springframework.stereotype.Service;

import com.google.gson.Gson;

@Service
public class JsonConverterService {
	public String convert(Object object) {
		return new Gson().toJson(object);
	}
}
