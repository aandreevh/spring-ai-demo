package demo.ai.spring.mcpClient5;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.json.jackson3.JacksonMcpJsonMapper;
import org.springframework.ai.mcp.SyncMcpToolCallback;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * Configure MCP Clients
 */
@Configuration
public class McpClientConfiguration {

    @Bean
    public McpJsonMapper mcpJsonMapper() {
        //Provide json mapper required for MCP tool input schemas
        return new JacksonMcpJsonMapper(new JsonMapper());
    }

    @Lazy //Required as our server is not yet initialized
    @Bean
    @Qualifier("local-mcp-server")
    public ToolsGroup localMcpServer(final McpJsonMapper mapper) {
        final var transport = HttpClientStreamableHttpTransport
                .builder("http://localhost:8080")
                .jsonMapper(mapper)
                .build();

        final var client = McpClient.sync(transport).build();
        client.initialize();

        return new ToolsGroup(client
                .listTools()
                .tools()
                .stream()
                .map(tool -> (ToolCallback) SyncMcpToolCallback.builder()
                        .mcpClient(client)
                        .tool(tool)
                        .build())
                .toList());
    }

    //TODO: explain optionally may be configured in application.yaml, for every ChatClient built
    @Bean
    @Qualifier("tools-file-system")
    public ToolsGroup fileSystemTools(final McpJsonMapper mapper,
                                      final @Value("${config.mcpClient.tools-file-system-directory}") String directory) {

        //TODO: explain tool to interact with file system
        return loadStdIOMCP(mapper,
                "npx", "-y", "@modelcontextprotocol/server-filesystem", directory);
    }

    @Bean
    @Qualifier("tools-sqlite")
    public ToolsGroup sqlite(final McpJsonMapper mapper,
                             final @Value("${config.mcpClient.tools-sqlite-db-file}") String database) {
        //TODO: explain sqlite mcp
        //TODO: may need to change your npx path to sth like this: C:\Program Files\nodejs\npx.cmd
        return loadStdIOMCP(mapper,
                "npx", "-y", "mcp-server-sqlite-npx", database);
    }

    private ToolsGroup loadStdIOMCP(final McpJsonMapper mapper, final String program, final String... args) {
        //TODO: Use stdio transport based MCP -> creates a process and interacts with IO to call tools
        final StdioClientTransport transport = new StdioClientTransport(
                new ServerParameters.Builder(program) /*TODO: requires npx installation (explain why npx)*/
                        .args(args)
                        .build(), mapper);

        // Create and initialize the MCP client
        var mcpClient = McpClient.sync(transport).build();
        mcpClient.initialize();

        //Provide tools to interact with server filesystem
        return new ToolsGroup(mcpClient
                .listTools() //List all available tools
                .tools() //Get all available tools
                .stream()
                .map(tool -> (ToolCallback) SyncMcpToolCallback.builder()
                        .mcpClient(mcpClient)
                        .tool(tool)
                        .build()) //Build tool callback from given tools (TODO:explain ToolCallback)
                .toList());
    }


    public record ToolsGroup(List<ToolCallback> tools) {
    }
}
