package com.enmasse.User_Service.dtos;

import lombok.Data;

@Data
public class LoginRequest {

    private String username;
    private String password;

}
