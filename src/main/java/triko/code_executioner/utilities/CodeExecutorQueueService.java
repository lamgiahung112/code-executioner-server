package triko.code_executioner.utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CodeExecutionRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.dto.responses.SaveTestCaseFileResult;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;

@Service
public class CodeExecutorQueueService {
	private static final Logger logger = LoggerFactory.getLogger(CodeExecutorQueueService.class);
	
	private final RabbitTemplate rabbitTemplate;
	private final JsonConverterService jsonConverterService;
	private final CodingProblemServiceInterface codingProblemService;
	
	@Value("${spring.rabbitmq.consumer-exchange-name}")
	private String consumerExchangeName;
	
	@Value("${spring.rabbitmq.routing-key}")
	private String routingKey;
	
	public CodeExecutorQueueService(RabbitTemplate rabbitTemplate, JsonConverterService jsonConverterService, CodingProblemServiceInterface codingProblemService) {
		this.rabbitTemplate = rabbitTemplate;
		this.jsonConverterService = jsonConverterService;
		this.codingProblemService = codingProblemService;
	}
	
	public void sendRequestMessageToQueue(CodeExecutionRequest message) {
		rabbitTemplate.convertAndSend(consumerExchangeName, routingKey, jsonConverterService.convert(message));
	}
	
	public void sendRequestMessageToQueue(SaveTestCaseFileRequest request) {
		rabbitTemplate.convertAndSend(consumerExchangeName, routingKey, jsonConverterService.convert(request));
	}
	
	@RabbitListener(queues = "${spring.rabbitmq.testcase-saving-service-exchange-name}", ackMode = "MANUAL")
	public Mono<?> receiveTestCaseSavingResultMessageFromQueue(Message message, Channel channel) {
		String testcaseData = new String(message.getBody(), StandardCharsets.UTF_8);
		logger.info("Received testcase message from rabbitmq: " + testcaseData);
		/*
		 * Handles Save TestCase File result messages
		 * */
		SaveTestCaseFileResult saveTestCaseFileResult = jsonConverterService.parseToObject(testcaseData, SaveTestCaseFileResult.class);
		if (saveTestCaseFileResult != null) {
			return codingProblemService.findById(saveTestCaseFileResult.problemId())
				.flatMap(problem -> {
					return codingProblemService.save(problem.withTestCasePath(saveTestCaseFileResult.testcasePath()))
						.then(Mono.empty());
				});
		}
		
		// Acknowledging that server has received message
		try {
	        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		return Mono.empty();
	}
	
	@RabbitListener(queues = "${spring.rabbitmq.code-execution-service-exchange-name}")
	public Mono<?> receiveCodeExecutionResultMessageFromQueue(Message message, Channel channel) {
		String codeExecutionResultData = new String(message.getBody(), StandardCharsets.UTF_8);
		logger.info("Received code execution message from rabbitmq: " + codeExecutionResultData);
		
		// Acknowledging that server has received message
		try {
	        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		return Mono.empty();
	}
}
