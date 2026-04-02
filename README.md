# Recommended Tutorials

* [**Spring AI Crash Course**](https://www.youtube.com/watch?v=FzLABAppJfM): A great starting point that covers the basics and moves into more advanced implementation patterns.
* [**Official Spring AI Documentation**](https://docs.spring.io/spring-ai/reference/): The definitive source for configuration and new feature releases.
  * [**Important AI Concepts**](https://docs.spring.io/spring-ai/reference/concepts.html): Really important fundamental AI concepts related to LLMSs

# Setup
* In order to start project run Bootstrap.java with provided **OPEN_ROUTER_API_KEY** env variable
  * If you want to use standard OpenAI, change base-url to (https://api.openai.com) from **application.yaml** and create and set API key from OpenAI
* In order to start http request you must provide **example.http-client.private.env.json** with your api key and optionally OpenAI endpoint if OpenRoute was not used (https://api.openai.com/v1)
  * You must also set (**Run With:**) profile in order to use the defined configuration variables from the created by you **http-client.private.env.json**

# Introduction to LLMs with Spring AI

This project serves as a practical introduction to integrating Large Language Models (LLMs) into Java applications using **Spring AI**. It demonstrates the evolution from simple chat interactions to type-safe structured data and autonomous tool execution.

## Prerequisites & Setup

The project is built on **Spring Boot 4.0.4** and **Spring AI 2.0.0-M3**.

### Dependencies (pom.xml)

* `spring-ai-starter-model-openai`: The core dependency. Even though we use OpenRouter, we use the OpenAI starter because OpenRouter is 100% API-compatible with OpenAI's specification.s
* `spring-boot-starter-web`: Provides the REST infrastructure.

## Step 0: Configuration (`application.yaml`)

Before writing code, we configure how the application communicates with the LLM provider.

### OpenRouter & Base URL
Instead of connecting directly to OpenAI, this project uses **OpenRouter** (`https://openrouter.ai/api/`).
* **Unified API**: It acts as a gateway to over 500+ models. You can swap `gpt-4o` for `claude-3-opus` just by changing the model name string.
* **Routing**: It supports "auto-routing" where it can pick the fastest or cheapest model available that meets your requirements.
* **Security**: Note the use of `${OPEN_ROUTER_API_KEY}`—always use environment variables to prevent accidental credential leaks.

### Tuning the Model: Temperature
**Temperature** (configured as `0.2` in this project) controls the "creativity" or randomness of the response:
* **Low (0.0 - 0.3)**: Deterministic and focused. The model always picks the highest-probability next token. Best for data extraction and coding.
* **High (0.7 - 1.0+)**: Creative and diverse. The model takes "risks" with lower-probability tokens. Best for brainstorming and creative writing.

### Managing Limits: Tokens
Tokens are the "currency" of LLMs (1,000 tokens ≈ 750 words).
* **max-tokens**: The total limit for the entire request (Prompt + Completion). If the sum exceeds this, the request fails.
* **max-completion-tokens**: A specific limit on how much text the model is allowed to generate. This is a safety rail to prevent the model from entering a "loop" or generating unnecessarily long responses that consume your budget.

---

## Step 1: Basic Chat Capabilities (`chatWithModel1`)

*Refer to: `ChatWithModelRestResource.java`*

The foundation of any LLM interaction is the **ChatClient**.

### Messaging Roles
Modern LLMs do not just take a single string; they accept an array of messages with distinct roles:
* **System**: The "Master Instruction." It defines the persona and constraints. The model is trained to prioritize this over user input (e.g., "Always say 'Bye!'").
* **User**: The human's input or request.
* **Assistant**: The model's own previous responses. When you build a "chat history," you append these to the message list.
* **Tool**: A special message type that contains the output of a function call, which the model then uses to generate its final answer.

### Streaming via Flux
In `ChatWithModelRestResource.java`, you'll see a comment about `.stream()`.
* **Standard `.call()`**: The server waits for the *entire* response to be generated (which can take 10+ seconds) before sending anything to the client.
* **Streaming `.stream()`**: Uses Server-Sent Events (SSE) to send tokens as they are generated. In Spring AI, this returns a `Flux<String>`, allowing for a highly responsive UI where text appears to "type" itself out.

### Try it out:
* **"Tell a joke."** - Tests basic generation + system instruction adherence.
* **"Give 10000 token essay on global warming"** - Demonstrates how `max-completion-tokens` truncates output.
* **Additional example available at rest-example/chatWithModel.http** - Demonstrates chatting via REST call
---

## Step 2: Structured Output & Efficiency (`structuredOutput2`)

*Refer to: `StructuredOutputRestResource.java`, `TaskClassificationDto.java`*

### Native vs. Best Effort
* **Best Effort**: Spring AI adds a massive hidden prompt: *"Return the following JSON schema: { ... } and do not include any other text."* This consumes many tokens and can still fail if the model adds "Sure, here is your JSON:".
* **Native (`ENABLE_NATIVE_STRUCTURED_OUTPUT`)**: Uses the provider's API (like OpenAI's `json_schema` response format). The model's output is physically constrained by the grammar of the schema. It is **cheaper** (fewer prompt tokens) and **100% reliable**.

### DTO Mappings & Reasoning
In `TaskClassificationDto.java`, we use `@JsonPropertyDescription`. This isn't just for documentation; **Spring AI sends these descriptions to the LLM**.
* **Context Window**: This is the model's memory (e.g., 128k tokens). Detailed DTOs use more of this window.
* **Reasoning field**: By including a `reason` field in the DTO, we force the model to explain its logic. This often leads to better results because the model "thinks" while generating the reason, which informs the value it chooses for the `priority` field.

### Try it out:
* **"My tooth hurts I have to go to the dentist."** - Checks if the model maps "pain" to `HIGH` priority.
* **Additional example available at rest-example/structuredOutput.http** - Demonstrates enforcing structured output in REST to GPT
---

## Step 3: Tool Execution (`toolExecution3`)

*Refer to: `ToolExecutionRestResource.java`, `CurrentDateTimeTool.java`, `AlarmTool.java`*

### Function Calling / Tools
Tools turn an LLM into an **Agent**. The model recognizes it cannot fulfill a request (like "What time is it?") with its training data, so it asks to call a tool.

### Enforced JSON Schema
In `AlarmTool.java`, the `@ToolParam` annotation is vital.
* It generates a JSON Schema for the tool's input.
* It allows us to enforce formats (e.g., `dd/MM/YYYY HH:mm`).
* Without this, the model might guess a format (like `YYYY-MM-DD`), which would cause your Java code to throw a `DateTimeParseException`.

### Try it out:
* **"Setup alarm for tomorrow on 2 PM for visiting the dentist."** - Demonstrates the model calling one tool to get the current time, calculating "tomorrow," and then calling the second tool to set the alarm.
* **Additional example available at rest-example/toolExecution.http** - Demonstrates tool call request from GPT
---

## Summary of Showcase Endpoints

* `GET /chatWithModel`: Simple prompt-response.
* `GET /structuredOutput`: Maps text to a `TaskClassificationDto`.
* `GET /toolExecution`: Allows the model to use `get_current_date_time` and `setup_alarm`.


# Next Time

* Advisors
* Memory
* Image & Audio

# In  Future

##  1
* MCP - Creation & Usage
* RAGs & Vector stores
* Different memory strategies - Pros & Cons

## 2
* Spring AI Support for testing
* Monitoring & Observability
* Security Strategies

## 3
* Simple Task planning
* Agents, Subagents