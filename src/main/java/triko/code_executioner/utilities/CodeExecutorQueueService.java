package triko.code_executioner.utilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import triko.code_executioner.dto.requests.HydratedCodeExecutionRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;

@Service
public class CodeExecutorQueueService {
	private static final Logger logger = LoggerFactory.getLogger(CodeExecutorQueueService.class);
	
	private final RabbitTemplate rabbitTemplate;
	private final JsonConverterService jsonConverterService;
	@Value("${spring.rabbitmq.exchange-name}")
	private String exchangeName;
	@Value("${spring.rabbitmq.routing-key}")
	private String routingKey;
	
	public CodeExecutorQueueService(RabbitTemplate rabbitTemplate, JsonConverterService jsonConverterService) {
		this.rabbitTemplate = rabbitTemplate;
		this.jsonConverterService = jsonConverterService;
	}
	
	public void sendCodeExecutionMessageToQueue(HydratedCodeExecutionRequest message) {
		logger.info("Sent message to rabbitmq: " + message);
		rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonConverterService.convert(message));
	}
	
	public void sendSaveTestCaseMessageToQueue(SaveTestCaseFileRequest request) {
		rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonConverterService.convert(request));
	}
	
	@RabbitListener(queues = "${spring.rabbitmq.exchange-name}")
	public void receiveMessageFromQueue(String message) {
		logger.info("Received message from rabbitmq: " + message);
		if (message.equals("Service started")) {
			rabbitTemplate.convertAndSend(exchangeName, routingKey, "Handshake");
		}
	}
}
