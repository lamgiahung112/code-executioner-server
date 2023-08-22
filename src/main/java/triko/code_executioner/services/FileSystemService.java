package triko.code_executioner.services;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import triko.code_executioner.dto.base.TestCase;
import triko.code_executioner.services.interfaces.FileSystemServiceInterface;
import triko.code_executioner.utilities.JsonConverterService;

@Service
public class FileSystemService implements FileSystemServiceInterface {
	@Autowired
	private JsonConverterService jsonConverterService;
	
	/**
	 * @return the path to the saved test cases
	 */
	@Override
	public String saveTestCases(List<TestCase> testcases) {
		String id = UUID.randomUUID().toString().substring(0, 10);
		
		try {
			String folderPath = "uploads/" + "testcases/" + id;
			File folder = new File(folderPath);
			String filePath = folderPath + "/test.json";
			File file = new File(filePath);
			
			if (!folder.exists()) {
				folder.mkdirs();
				file.createNewFile();
			}
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
			String testcaseDataInJsonFormat = jsonConverterService.convert(testcases);
			
			writer.write(testcaseDataInJsonFormat);
			writer.close();
		
			return filePath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
