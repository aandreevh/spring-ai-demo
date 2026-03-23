package demo.ai.spring.toolExecution3.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;

import java.time.LocalDateTime;

public class CurrentDateTimeTool {
    private static final Logger LOG = LoggerFactory.getLogger(CurrentDateTimeTool.class);
    @Tool(name = "get_current_date_time", description = "Provides current date and time")
    public String getCurrentDateTime() {
        LOG.info("Current date time taken.");
        return LocalDateTime.now().toString();
    }
}
