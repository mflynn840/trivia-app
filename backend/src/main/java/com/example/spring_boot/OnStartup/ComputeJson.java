package com.example.spring_boot.OnStartup;



import com.example.spring_boot.Service.QuestionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ComputeJson implements CommandLineRunner {

    private final QuestionService questionService;
    private final ObjectMapper objectMapper;

    public ComputeJson(QuestionService questionService, ObjectMapper objectMapper) {
        this.questionService = questionService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void run(String... args) throws Exception {
        // Call the service layer to compute the data
        Map<String, Map<String, Long>> counts = questionService.getQuestionCountsByCategoryAndDifficulty();

        // Convert to JSON
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(counts);

        // Print to terminal
        System.out.println("Question counts JSON:\n" + json);
    }
}