package triko.code_executioner.utilities;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.rabbitmq.client.Channel;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import triko.code_executioner.dto.requests.CodeExecutionRequest;
import triko.code_executioner.dto.requests.SaveTestCaseFileRequest;
import triko.code_executioner.dto.responses.CodeExecutionResult;
import triko.code_executioner.dto.responses.SaveTestCaseFileResult;
import triko.code_executioner.models.enums.SubmissionStatus;
import triko.code_executioner.services.interfaces.CodingProblemServiceInterface;
import triko.code_executioner.services.interfaces.UserCodeSubmissionServiceInterface;

@Service
public class CodeExecutorQueueService {
	private static final Logger logger = LoggerFactory.getLogger(CodeExecutorQueueService.class);

	@Autowired
	private RabbitTemplate rabbitTemplate;
	@Autowired
	private JsonConverterService jsonConverterService;
	@Autowired
	private CodingProblemServiceInterface codingProblemService;
	@Autowired
	private UserCodeSubmissionServiceInterface userCodeSubmissionService;

	@Value("${spring.rabbitmq.consumer-exchange-name}")
	private String consumerExchangeName;

	@Value("${spring.rabbitmq.routing-key}")
	private String routingKey;

	public void sendRequestMessageToQueue(CodeExecutionRequest message) {
		rabbitTemplate.convertAndSend(consumerExchangeName, routingKey, jsonConverterService.convert(message));
	}

	public void sendRequestMessageToQueue(SaveTestCaseFileRequest request) {
		rabbitTemplate.convertAndSend(consumerExchangeName, routingKey, jsonConverterService.convert(request));
	}

	@RabbitListener(queues = "${spring.rabbitmq.testcase-saving-service-exchange-name}", ackMode = "MANUAL")
	public Mono<?> receiveTestCaseSavingResultMessageFromQueue(Message message, Channel channel) throws IOException {
		String testcaseData = new String(message.getBody(), StandardCharsets.UTF_8);
		logger.info("Received testcase message from rabbitmq: " + testcaseData);
		/*
		 * Handles Save TestCase File result messages
		 * */
		SaveTestCaseFileResult saveTestCaseFileResult = jsonConverterService.parseToObject(testcaseData, SaveTestCaseFileResult.class);
		if (saveTestCaseFileResult != null) {
			return codingProblemService.findById(saveTestCaseFileResult.problemId())
				.flatMap(problem -> {
					problem.setPending(false);
					return codingProblemService.save(problem)
						.then(Mono.empty());
				});
		}

		return Mono.empty();
	}

	@RabbitListener(queues = "${spring.rabbitmq.code-execution-service-exchange-name}", ackMode = "MANUAL")
	public Mono<?> receiveCodeExecutionResultMessageFromQueue(Message message, Channel channel) throws IOException {
		String codeExecutionResultData = new String(message.getBody(), StandardCharsets.UTF_8);
		logger.info("Received code execution message from rabbitmq: " + codeExecutionResultData);
		
		CodeExecutionResult result = jsonConverterService.parseToObject(codeExecutionResultData, CodeExecutionResult.class);
		SubmissionStatus status = SubmissionStatus.PASSED;
		
		if (result != null) {
			return userCodeSubmissionService.findById(result.submissionId())
					.flatMap(submission -> {
						submission.setCompletedAt(result.completedAt());
						submission.setStatus(status);
						return userCodeSubmissionService.save(submission)
								.then(Mono.empty());
					});
		}

		channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		return Mono.empty();
	}
}
