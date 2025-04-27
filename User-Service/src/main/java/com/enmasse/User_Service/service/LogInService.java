package com.enmasse.User_Service.service;

import com.enmasse.User_Service.config.KeycloakFeignClient;
import com.enmasse.User_Service.dtos.LoginRequest;
import com.enmasse.User_Service.dtos.LoginResponse;
import com.enmasse.User_Service.dtos.UserInfoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;


@Slf4j
@Service
public class LogInService {

    @org.springframework.beans.factory.annotation.Value("${app.keycloak.client-secret}")
    private String clientSecret;

    @org.springframework.beans.factory.annotation.Value("${app.keycloak.grant-type}")
    private String grantType;

    @org.springframework.beans.factory.annotation.Value("${app.keycloak.client-id}")
    private String clientId;

    @org.springframework.beans.factory.annotation.Value("${app.keycloak.scope}")
    private String scope;

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
        formData.add("scope", scope);

        ResponseEntity<LoginResponse> response = keycloakFeignClient.login(formData);

        return ResponseEntity.ok(response.getBody());
    }

    public void logout(String idToken) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("id_token_hint", idToken);
        formData.add("post_logout_redirect_uri", "http://localhost:8761"); //the redirection should be the homepage

        ResponseEntity<String> response = keycloakFeignClient.logout(formData);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Logout successful!");
        } else {
            System.out.println("Logout failed: " + response.getStatusCode());
        }
    }


    public ResponseEntity<UserInfoResponse> getUserInfo(String accessToken) {
        log.info("Get user info using Bearer token: " + accessToken);
        ResponseEntity<UserInfoResponse> response = keycloakFeignClient.getUserInfo(accessToken);
        log.info(" token: " + accessToken);
        return ResponseEntity.ok(response.getBody());
    }

//    public ResponseEntity<LoginResponse> refresh(String refreshToken) {
//        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
//        formData.add("refresh_token", refreshToken);
//        formData.add("client_id", clientId);
//        formData.add("client_secret", clientSecret);
//        formData.add("grant_type", grantType);
//        formData.add("scope", scope);
//
//
//    }



}
