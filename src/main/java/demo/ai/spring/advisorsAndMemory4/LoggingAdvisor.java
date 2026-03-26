package demo.ai.spring.advisorsAndMemory4;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.messages.Message;

/**
 * Responsible for logging request and response.
 */
public class LoggingAdvisor implements CallAdvisor {
  private static final Logger LOG = LoggerFactory.getLogger(LoggingAdvisor.class);

  @Override
  public ChatClientResponse adviseCall(final ChatClientRequest chatClientRequest,
      final CallAdvisorChain callAdvisorChain) {

    final StringBuilder chatContentBuilder = new StringBuilder(System.lineSeparator());

    logRequest(chatClientRequest, chatContentBuilder);

    final ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);

    logResponse(response, chatContentBuilder);

    LOG.info(chatContentBuilder.toString());
    return response;
  }

  private void logRequest(final ChatClientRequest request, final StringBuilder chatContentBuilder) {
    for (final Message message : request.prompt().getInstructions()) {
      appendChatContent(message, chatContentBuilder);
    }

  }

  private void logResponse(final ChatClientResponse response, final StringBuilder chatContentBuilder) {
    if (response.chatResponse() != null && response.chatResponse().getResult() != null) {
      appendChatContent(response.chatResponse().getResult().getOutput(), chatContentBuilder);
    }
  }

  private void appendChatContent(final Message message, final StringBuilder chatContentBuilder) {
    chatContentBuilder
        .append(message.getMessageType().getValue().toUpperCase()).append(": ")
        .append(message.getText())
        .append(System.lineSeparator());
  }

  @Override
  public String getName() {
    return "LoggingAdvisor";
  }

  @Override
  public int getOrder() {
    //TODO: Explain order
    return Advisor.DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER + 1;
  }
}
