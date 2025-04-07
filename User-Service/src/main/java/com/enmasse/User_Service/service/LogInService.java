package com.enmasse.User_Service.service;

import com.enmasse.User_Service.config.KeycloakFeignClient;
import com.enmasse.User_Service.dtos.LoginRequest;
import com.enmasse.User_Service.dtos.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@Service
public class LogInService {

    @org.springframework.beans.factory.annotation.Value("${app.keycloak.client-secret}")
    private String clientSecret;

    @org.springframework.beans.factory.annotation.Value("${app.keycloak.grant-type}")
    private String grantType;

    @org.springframework.beans.factory.annotation.Value("${app.keycloak.client-id}")
    private String clientId;

    private final KeycloakFeignClient keycloakFeignClient;

    public LogInService(KeycloakFeignClient keycloakFeignClient) {
        this.keycloakFeignClient = keycloakFeignClient;
    }

    public ResponseEntity<LoginResponse> login(LoginRequest request) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", request.getUsername());
        formData.add("password", request.getPassword());
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", grantType);

        ResponseEntity<LoginResponse> response = keycloakFeignClient.login(formData);

        return ResponseEntity.ok(response.getBody());
    }
}
