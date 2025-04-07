package com.enmasse.User_Service.controller;

import com.enmasse.User_Service.dtos.LoginRequest;
import com.enmasse.User_Service.dtos.LoginResponse;
import com.enmasse.User_Service.service.LogInService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
