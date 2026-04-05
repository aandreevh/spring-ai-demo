package demo.ai.spring.mcpClient5;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

@Service
public class JavaScriptExecutor {
    private static final Logger LOG = LoggerFactory.getLogger(JavaScriptExecutor.class);
    private static final ScriptEngine JS_ENGINE = new ScriptEngineManager().getEngineByName("nashorn");

    @McpTool(name = "runJs", description = "Executes simple JavaScript code")
    public String execute(final String code) {
        try {
            final Object result = JS_ENGINE.eval(code);
            LOG.info("Executed the following JavaScript code:\n\n{}\n\tResult:{}", code, result);
            return String.valueOf(result);
        } catch (final Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
