package demo.ai.spring.structuredOutput2;

import demo.ai.spring.structuredOutput2.task.TaskClassificationDto;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* Example prompts:
*   - My tooth hurts I have to go to the dentist.
*   - I have to go to the bathroom
*   - I should clean my wardrobe when I have free time
*   - Creating a Spring AI introduction demo project for demonstrating chat, structured output and tool capabilities to coworkers.
* */
@RestController
@RequestMapping("/structuredOutput")
public class StructuredOutputRestResource {

    private static final String SYSTEM_INSTRUCTIONS = """
            Act as a Task Auditor. Evaluate the input strictly:
            
            Priority: Map criticality.
            
            Effort: in hours. Increment by 0.1.
            
            Reason: Explain why are the specific priority and effort chosen.
            Return only valid JSON.""";

    private final ChatClient client;

    @Autowired
    public StructuredOutputRestResource(final ChatClient.Builder builder) {
        this.client = builder
                //TODO: explain structured output (difference between native and best effort) - dont forget token efficiency
                //TODO: maybe good time to explain LLM capabilities: token context window, reasoning, tool using
                .defaultAdvisors(AdvisorParams.ENABLE_NATIVE_STRUCTURED_OUTPUT)
                .build();
    }

    @GetMapping
    public TaskClassificationDto chat(final @RequestBody String prompt) {
        return client.prompt()
                .system(SYSTEM_INSTRUCTIONS)
                .user(prompt)
                .call()
                .entity(TaskClassificationDto.class);
    }
}
