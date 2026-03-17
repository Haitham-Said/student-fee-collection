package com.task.feeservice.client;

import com.task.feeservice.client.dto.StudentLookupDto;
import com.task.feeservice.exception.StudentNotFoundException;
import com.task.feeservice.exception.StudentServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Component
public class StudentServiceClient {

    private final RestClient restClient;
    public StudentServiceClient(
            @Value("${fee-service.student-service.base-url}") String baseUrl,
            @Value("${fee-service.rest.connect-timeout-ms}") int connectTimeoutMs,
            @Value("${fee-service.rest.read-timeout-ms}") int readTimeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(connectTimeoutMs);
        requestFactory.setReadTimeout(readTimeoutMs);

        this.restClient = RestClient.builder()
                .requestFactory(requestFactory)
                .baseUrl(baseUrl)
                .build();
    }

    public StudentLookupDto getStudentById(String studentId) {
        try {
            return restClient.get()
                    .uri("/internal/students/{studentId}", studentId)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            throw new StudentNotFoundException(studentId);
                        }
                    })
                    .body(StudentLookupDto.class);
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new StudentNotFoundException(studentId);
            }
            throw new StudentServiceUnavailableException("Failed to call student-service: " + ex.getMessage(), ex);
        } catch (RestClientException ex) {
            throw new StudentServiceUnavailableException("Student-service is unavailable", ex);
        }
    }
}
