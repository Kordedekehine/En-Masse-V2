package com.enmasse.Order_Service.client;

import com.enmasse.Order_Service.dtos.UserInfoResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "${user-service.name}", url = "${user-service.url}")
public interface UserClient {

    @GetMapping("/userinfo")
    ResponseEntity<UserInfoResponse> getUserInfo(HttpServletRequest request);

 }
