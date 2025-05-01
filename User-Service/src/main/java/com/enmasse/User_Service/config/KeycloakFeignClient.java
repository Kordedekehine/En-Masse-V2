package com.enmasse.User_Service.config;

import com.enmasse.User_Service.dtos.LoginResponse;
import com.enmasse.User_Service.dtos.UserInfoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(name = "keycloak-client", url = "${app.keycloak.login.url}")
public interface KeycloakFeignClient {

    @PostMapping(value = "/token", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<LoginResponse> login(@RequestBody MultiValueMap<String, String> form);

    @GetMapping(value = "/userinfo")
    ResponseEntity<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String bearerToken);

    @PostMapping(value = "/logout", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<String> logout(@RequestBody MultiValueMap<String, String> formData);


}
