package demo.ai.spring.mcpClient5;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/mcpClient")
public class MCPClientRestResource {
  private final ChatClient chatClient;

  @Autowired
  public MCPClientRestResource(final ChatClient.Builder builder,
      final @Qualifier("tools-file-system") List<ToolCallback> fileSystemTools) {

    this.chatClient = builder
        .defaultToolCallbacks(fileSystemTools) //register tools to be used by client
        .build();
  }

  @GetMapping
  public String mcpClient(final @RequestBody String prompt) {
    return chatClient.prompt()
        .user(prompt) //TODO: Optionally register tools here
        .call()
        .content();
  }
}
