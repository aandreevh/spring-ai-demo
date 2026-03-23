package demo.ai.spring.toolExecution3;

import demo.ai.spring.toolExecution3.tools.AlarmTool;
import demo.ai.spring.toolExecution3.tools.CurrentDateTimeTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Example prompts:
 *   - What is the current date and time?
 *   - What day would it be tomorrow?
 *   - Setup alarm for tomorrow on 2 PM for visiting the dentist.
 *   - Setup alarm for on 17/2/2027 12 AM to remind me for Emma's birthday
 * */
@RestController
@RequestMapping("/toolExecution")
public class ToolExecutionRestResource {
    private static final String SYSTEM_INSTRUCTIONS = """
            Provide any date information in format: dd/MM/YYYY
            Provide any time information in format: HH:mm""";

    private final ChatClient client;

    @Autowired
    public ToolExecutionRestResource(final ChatClient.Builder builder) {
        this.client = builder
                .build();
    }

    @GetMapping
    public String chat(final @RequestBody String prompt) {
        return client.prompt()
                .user(prompt)
                .system(SYSTEM_INSTRUCTIONS)
                //TODO: explain tools
                .tools(new CurrentDateTimeTool(), new AlarmTool())
                .call()
                .content();
    }
}
