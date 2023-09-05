package triko.code_executioner.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.HydratedCodeExecutionRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.dto.responses.SaveTestCaseFileResult;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;

@Service
public class CodeExecutorQueueService {
	private static final Logger logger = LoggerFactory.getLogger(CodeExecutorQueueService.class);
	
	private final RabbitTemplate rabbitTemplate;
	private final JsonConverterService jsonConverterService;
	private final CodingProblemServiceInterface codingProblemService;
	
	@Value("${spring.rabbitmq.code-execution-service-exchange-name}")
	private String codeExecutionServiceExchangeName;
	
	@Value("${spring.rabbitmq.consumer-exchange-name}")
	private String consumerExchangeName;
	
	@Value("${spring.rabbitmq.testcase-saving-service-exchange-name}")
	private String testcaseSavingServiceExchangeName;
	
	@Value("${spring.rabbitmq.routing-key}")
	private String routingKey;
	
	public CodeExecutorQueueService(RabbitTemplate rabbitTemplate, JsonConverterService jsonConverterService, CodingProblemServiceInterface codingProblemService) {
		this.rabbitTemplate = rabbitTemplate;
		this.jsonConverterService = jsonConverterService;
		this.codingProblemService = codingProblemService;
	}
	
	public void sendRequestMessageToQueue(HydratedCodeExecutionRequest message) {
		rabbitTemplate.convertAndSend(consumerExchangeName, routingKey, jsonConverterService.convert(message));
	}
	
	public void sendRequestMessageToQueue(SaveTestCaseFileRequest request) {
		rabbitTemplate.convertAndSend(consumerExchangeName, routingKey, jsonConverterService.convert(request));
	}
	
	@RabbitListener(queues = "${spring.rabbitmq.testcase-saving-service-exchange-name}")
	public Mono<?> receiveTestCaseSavingResultMessageFromQueue(String message) {
		logger.info("Received testcase message from rabbitmq: " + message);
		/*
		 * Handles Save TestCase File result messages
		 * */
		SaveTestCaseFileResult saveTestCaseFileResult = jsonConverterService.parseToObject(message, SaveTestCaseFileResult.class);
		if (saveTestCaseFileResult != null) {
			return codingProblemService.findById(saveTestCaseFileResult.problemId())
				.flatMap(problem -> {
					return codingProblemService.save(problem.withTestCasePath(saveTestCaseFileResult.testcasePath()))
						.then(Mono.empty());
				});
		}
		
		return Mono.empty();
	}
	
	@RabbitListener(queues = "${spring.rabbitmq.code-execution-service-exchange-name}")
	public Mono<?> receiveCodeExecutionResultMessageFromQueue(String message) {
		logger.info("Received code execution message from rabbitmq: " + message);
		
		return Mono.empty();
	}
}
