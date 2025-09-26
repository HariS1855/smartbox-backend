package com.smartbox.SmartBox.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.Map;

@Service
public class FlaskClientService {

    private final RestTemplate restTemplate;

    @Value("${flask.api.url}")
    private String flaskApiUrl;

    public FlaskClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> analyzeImage(String imagePath) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            File file = new File(imagePath);
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new FileSystemResource(file));

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    flaskApiUrl, HttpMethod.POST, requestEntity, Map.class
            );

            System.out.println("[DEBUG] Flask response: " + response.getBody());
            return response.getBody();
        } catch (Exception e) {
            System.err.println("Flask API call failed: " + e.getMessage());
            return null;
        }
    }

}
