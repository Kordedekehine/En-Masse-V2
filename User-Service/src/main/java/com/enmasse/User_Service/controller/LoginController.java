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
@RequestMapping("/api/v1/enMasse")
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

    @GetMapping("/getUser")
    public ResponseEntity<UserInfoResponse> getUserInfo (@RequestHeader("Authorization")String token) throws Exception {

        ResponseEntity<UserInfoResponse> response = null;
        response = logInService.getUserInfo(token);
        return response;
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String idToken = authHeader.substring(7);
            log.info("Logout request: " + idToken);
            logInService.logout(idToken);
            return ResponseEntity.ok("Logout successful");
        } else {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }
    }

}
