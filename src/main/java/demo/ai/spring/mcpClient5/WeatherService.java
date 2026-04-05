package demo.ai.spring.mcpClient5;


import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

@Service
public class WeatherService {

    @McpTool(description = "Get temperature for a city")
    public String getTemperature(
            @McpToolParam(description = "City name")
            String city) {

        return "Temperature in " + city + " is 22°C";
    }
}