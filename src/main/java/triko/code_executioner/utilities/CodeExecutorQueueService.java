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
import triko.code_executioner.models.DCodingProblem;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;

@Service
public class CodeExecutorQueueService {
	private static final Logger logger = LoggerFactory.getLogger(CodeExecutorQueueService.class);
	
	private final RabbitTemplate rabbitTemplate;
	private final JsonConverterService jsonConverterService;
	private final CodingProblemServiceInterface codingProblemService;
	
	@Value("${spring.rabbitmq.exchange-name}")
	private String exchangeName;
	@Value("${spring.rabbitmq.routing-key}")
	private String routingKey;
	
	public CodeExecutorQueueService(RabbitTemplate rabbitTemplate, JsonConverterService jsonConverterService, CodingProblemServiceInterface codingProblemService) {
		this.rabbitTemplate = rabbitTemplate;
		this.jsonConverterService = jsonConverterService;
		this.codingProblemService = codingProblemService;
	}
	
	public void sendCodeExecutionMessageToQueue(HydratedCodeExecutionRequest message) {
		logger.info("Sent message to rabbitmq: " + message);
		rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonConverterService.convert(message));
	}
	
	public void sendSaveTestCaseMessageToQueue(SaveTestCaseFileRequest request) {
		rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonConverterService.convert(request));
	}
	
	@RabbitListener(queues = "${spring.rabbitmq.exchange-name}")
	public Mono<?> receiveMessageFromQueue(String message) {
		logger.info("Received message from rabbitmq: " + message);
		if (message.equals("Service started")) {
			rabbitTemplate.convertAndSend(exchangeName, routingKey, "Handshake");
			return Mono.empty();
		}
		
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
}
