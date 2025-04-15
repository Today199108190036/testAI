package com.testai.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class Controller {

    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/apps/87064923d41f491599c6c6372f0a5a1d/completion"; // 替换为实际API地址
    private static final String API_KEY = ""; // 替换为您的API密钥

    @FXML
    private TextField questionField;

    @FXML
    private TextArea answerArea;

    @FXML
    public void submitQuestion() {
        String question = questionField.getText().trim();
        if (!question.isEmpty()) {
            new Thread(() -> {
                try {
                    String answer = callQnAAPI(question);
                    Platform.runLater(() -> answerArea.setText(answer));
                } catch (Exception e) {
                    Platform.runLater(() -> answerArea.setText("Error: " + e.getMessage()));
                }
            }).start();
        } else {
            answerArea.setText("Please enter a question.");
        }
    }

    private String callQnAAPI(String question) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String jsonInputString = "{\"question\": \"" + question + "\"}";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(API_URL);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Authorization", "Bearer " + API_KEY);

            StringEntity entity = new StringEntity(jsonInputString);
            request.setEntity(entity);
            System.out.println(jsonInputString);
            HttpResponse response = httpClient.execute(request);
            String jsonResponse = EntityUtils.toString(response.getEntity(), "UTF-8");

            JsonNode rootNode = mapper.readTree(jsonResponse);
            System.out.println(rootNode.path("answer").asText());
            return rootNode.path("answer").asText();
        }
    }
}


