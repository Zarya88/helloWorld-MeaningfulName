package com.example.ex1;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest //(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PollAppTests {

    private final RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:8080")
            .defaultHeader(HttpHeaders.ACCEPT, "application/json")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
            .build();

    @Test
    @Order(1)
    void canCreateUser() {
        var res = restClient.post()
                .uri("/user/create")
                .body("{ \"username\": \"Mary\", \"email\": \"mary@example.com\" }")
                .retrieve()
                .toBodilessEntity();

        assertEquals(HttpStatus.OK, res.getStatusCode());
    }

    @Test
    @Order(2)
    void canListFirstUser() {
        var res = restClient.get()
                .uri("/user/all")
                .retrieve()
                .toEntity(String.class);

        assertEquals("{\"1\":{\"userId\":1,\"username\":\"Mary\",\"email\":\"mary@example.com\",\"password\":null,\"polls\":null}}", res.getBody());
    }

}