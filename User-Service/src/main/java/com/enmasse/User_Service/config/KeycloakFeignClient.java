package com.enmasse.User_Service.config;

import com.enmasse.User_Service.dtos.LoginResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "keycloak-client", url = "${app.keycloak.login.url}")
public interface KeycloakFeignClient {

    @PostMapping(consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<LoginResponse> login(@RequestBody MultiValueMap<String, String> form);
}
