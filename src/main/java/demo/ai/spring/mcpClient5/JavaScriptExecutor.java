package demo.ai.spring.mcpClient5;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Service
public class JavaScriptExecutor {
    private static final ScriptEngine JS_ENGINE = new ScriptEngineManager().getEngineByName("nashorn");

    @McpTool(name = "runJs", description = "Executes simple JavaScript code")
    public String execute(final String code) {
        try {
            final Object result = JS_ENGINE.eval(code);
            return String.valueOf(result);
        } catch (final Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
