package demo.ai.spring.advisorsAndMemory4;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advisorsAndMemory")
public class AdvisorsAndMemoryRestResource {


  private final ChatClient chatClient;
  private final ChatMemory chatMemory;

  @Autowired
  public AdvisorsAndMemoryRestResource(final ChatClient.Builder builder, final ChatMemory chatMemory) {
    this.chatClient = builder.build();
    this.chatMemory = chatMemory;
  }

  @GetMapping("/{contextId}")
  public String chat(final @RequestBody String prompt, final @PathVariable("contextId") String contextId) {
    return chatClient.prompt()
        .user(prompt)
        .advisors( //TODO: explain advisors
            //Loads all previous messages first and saves the new messages after
            MessageChatMemoryAdvisor.builder(chatMemory).conversationId(contextId).build(), //TODO: explain conversation id
            //Logs messages loaded from memory as well as new user message and response from chat
            new LoggingAdvisor()) //TODO: explain log advisor
        .call()
        .content();
  }
}
