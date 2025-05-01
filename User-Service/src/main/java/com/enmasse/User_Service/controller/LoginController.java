package com.enmasse.User_Service.controller;

import com.enmasse.User_Service.dtos.LoginRequest;
import com.enmasse.User_Service.dtos.LoginResponse;
import com.enmasse.User_Service.dtos.UserInfoResponse;
import com.enmasse.User_Service.service.LogInService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class LoginController {

    @Autowired
    private LogInService logInService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login (HttpServletRequest request, @RequestBody LoginRequest loginRequest) throws Exception {
        log.info("Executing login");

        ResponseEntity<LoginResponse> response = null;
        response = logInService.login(loginRequest);

        return response;
    }


    @GetMapping("/userinfo")
    public ResponseEntity<UserInfoResponse> getUserInfo(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().build();
        }

        String accessToken = authHeader.substring(7);
        log.info("Extracted token: {}", accessToken);

        ResponseEntity<UserInfoResponse> response = logInService.getUserInfo("Bearer " + accessToken);
        return ResponseEntity.ok(response.getBody());
    }


    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String refreshToken) {

        if (refreshToken != null && refreshToken.startsWith("Bearer ")) {
            String refreshTokens = refreshToken.substring(7);
            log.info("Logout request: " + refreshTokens);
            logInService.logout(refreshTokens);
            return ResponseEntity.ok("Logout successful");
        } else {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
    }

}
