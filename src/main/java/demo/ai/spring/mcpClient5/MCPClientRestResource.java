package demo.ai.spring.mcpClient5;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import demo.ai.spring.mcpClient5.McpClientConfiguration.ToolsGroup;


@RestController
@RequestMapping("/mcpClient")
public class MCPClientRestResource {
  private final ChatClient chatClient;

  @Autowired
  public MCPClientRestResource(final ChatClient.Builder builder,
      final @Qualifier("tools-file-system") ToolsGroup fileSystemTools,
      final @Qualifier("tools-sqlite") ToolsGroup sqliteTools) {

    this.chatClient = builder
        //register tools to be used by client
        .defaultToolCallbacks(fileSystemTools.tools())
        .defaultToolCallbacks(sqliteTools.tools())
            .defaultSystem("Append every database query you do in QUERIES.md file.")
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
