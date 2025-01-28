package com.github.farmplus.test;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

public class ApiTest {

    public static void main(String[] args) {
        // RestTemplate 객체 생성
        RestTemplate restTemplate = new RestTemplate();

        // 회원가입 요청 URL 설정
        String url = "http://localhost:8080/auth/sign-up";

        // 회원가입 데이터 (JSON 형식)
        String jsonPayload = "{"
                + "\"name\": \"John\","
                + "\"nickname\": \"johnny\","
                + "\"email\": \"john@example.com\","
                + "\"password\": \"password123\","
                + "\"phoneNumber\": \"1234567890\","
                + "\"address\": \"123 Main St\""
                + "}";

        // 요청 헤더 설정 (Content-Type: application/json)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // HttpEntity 객체 생성 (헤더와 데이터를 포함)
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        // POST 요청 보내기
        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                String.class
        );

        // 응답 상태 코드와 응답 본문 출력
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());
    }
}

