package com.bohemian.intellect.service;

import com.bohemian.intellect.model.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final OllamaChatModel ollamaChatModel;
    private final ObjectMapper objectMapper;
    private final ResourceLoader resourceLoader;


    public AIService(OllamaChatModel ollamaChatModel, ObjectMapper objectMapper, ResourceLoader resourceLoader) {
        this.ollamaChatModel = ollamaChatModel;
        this.objectMapper = objectMapper;
        this.resourceLoader = resourceLoader;
    }

    public List<Question> generateAnswers(Map<String, String> placeholders) {
        try {
            Resource resource = resourceLoader.getResource("classpath:template/prompt/generate-questions.txt");
            String prompt = Files.readString(resource.getFile().toPath());
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                prompt = prompt.replace("{{" + entry.getKey() + "}}", entry.getValue());
            }
            String call = ollamaChatModel.call(prompt);
            int start = call.indexOf('[');
            int end = call.lastIndexOf(']')+1;
            call = call.substring(start, end);
            return objectMapper.readValue(call, new TypeReference<List<Question>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
