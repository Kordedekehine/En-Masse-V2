package com.enmasse.Payment_Service.client;

import com.enmasse.Payment_Service.dtos.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "${user-service.name}", url = "${user-service.url}")
public interface UserClient {

    @GetMapping("/userinfo")
    ResponseEntity<UserInfoResponse> getUserInfo(@RequestHeader("Authorization") String authHeader);


}
