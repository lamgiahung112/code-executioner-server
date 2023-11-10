package triko.code_executioner.utilities.converters;

import org.springframework.core.convert.converter.Converter;

import com.google.gson.Gson;

import triko.code_executioner.dto.base.TestCase;

public class StringToTestCaseConverter implements Converter<String, TestCase> {
	@Override
	public TestCase convert(String source) {
        try {
            return new Gson().fromJson(source, TestCase.class);
        } catch (Exception e) {
            // Handle the exception (log, throw, etc.)
            throw new IllegalArgumentException("Error converting String to TestCase", e);
        }
    }
}
