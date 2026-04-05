package demo.ai.spring.mcpClient5;

import demo.ai.spring.mcpClient5.McpClientConfiguration.ToolsGroup;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Lazy
@RestController
@RequestMapping("/mcpClient")
public class MCPClientRestResource {
    private final ChatClient chatClient;

    @Autowired
    public MCPClientRestResource(
            @Value("classpath:GENERAL_INSTRUCTIONS.md") final Resource instructions,
            final ChatClient.Builder builder,
            final @Qualifier("tools-file-system") ToolsGroup fileSystemTools,
            final @Qualifier("tools-sqlite") ToolsGroup sqliteTools,
            final @Qualifier("local-mcp-server") ToolsGroup localMcpTools) throws IOException {

        try (final InputStream in = instructions.getInputStream()) {
            this.chatClient = builder
                    //register tools to be used by client
                    .defaultToolCallbacks(fileSystemTools.tools())
                    .defaultToolCallbacks(sqliteTools.tools())
                    .defaultToolCallbacks(localMcpTools.tools())
                    .defaultSystem(new String(in.readAllBytes(), StandardCharsets.UTF_8))
                    .build();
        }

    }

    @GetMapping
    public String mcpClient(final @RequestBody String prompt) {
        return chatClient.prompt()
                .user(prompt) //TODO: Optionally register tools here
                .call()
                .content();
    }
}
