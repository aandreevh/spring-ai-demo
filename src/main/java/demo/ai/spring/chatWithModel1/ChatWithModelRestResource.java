package demo.ai.spring.chatWithModel1;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
* Example prompts:
*   - Tell a joke.
*   - How much is 2 + 2
*   - Give 10000 token essay on global warming (check token count via: https://platform.openai.com/tokenizer)
 * */
@RestController
@RequestMapping("/chatWithModel")
public class ChatWithModelRestResource {

    private final ChatClient client;

    @Autowired
    public ChatWithModelRestResource(ChatClient.Builder builder) {
        //Default builder provided by Spring AI autoconfiguration
        this.client = builder.build();
    }

    @GetMapping
    public String chat(final @RequestBody String prompt) {
        return client.prompt()
                //TODO: explain system, user, assistant and tool messaging roles
                .system("Always say 'Bye!' on a new line in the end.")
                .user(prompt)
                //.stream() TODO: explain streaming capabilities via Flux
                .call()
                .content();
    }
}
