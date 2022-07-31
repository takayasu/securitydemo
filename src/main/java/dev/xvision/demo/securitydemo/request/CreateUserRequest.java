package dev.xvision.demo.securitydemo.request;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String userName;
    private String password;
    private String[] roles;
}
