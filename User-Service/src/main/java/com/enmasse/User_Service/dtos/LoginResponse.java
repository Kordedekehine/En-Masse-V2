package com.enmasse.User_Service.dtos;

import lombok.Data;

@Data
public class LoginResponse {

    private String access_token;
    private String refresh_token;
    private String expires_in;
    private String refresh_expires_in;
    private String id_token;
    private String token_type;
}
