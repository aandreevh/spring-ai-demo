package demo.ai.spring.toolExecution3.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class AlarmTool {
    private static final Logger LOG = LoggerFactory.getLogger(AlarmTool.class);

    //TODO: explain possibility for enforced JSON schema on parameters for tool calling
    @Tool(name = "setup_alarm", description = "Sets up alarm with a message for the provided dateTime")
    public void setupAlarm(
            @ToolParam(description = "Date time for setting up the alarm in format dd/MM/YYYY HH:mm. Do not include any other symbols") final String dateTime,
            @ToolParam(description = "Message to be shown when alarm triggers") final String message) {
        LOG.info("Alarm set for {} with message: {}", dateTime, message);
    }
}
