package dev.xvision.demo.securitydemo.controller;

import dev.xvision.demo.securitydemo.bean.UserInfo;
import dev.xvision.demo.securitydemo.request.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
public class UserController {
    @Autowired
    private UserDetailsManager users;

    @PostMapping("/users/create")
    public UserInfo createSUser(@RequestBody CreateUserRequest request){

        UserInfo info = new UserInfo();
        info.setUserName(request.getUserName());
        info.setRoles(Arrays.stream(request.getRoles()).map(Object::toString).collect(Collectors.joining(",")));

        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

        UserDetails user = User.withUsername(request.getUserName())
                .password(encoder.encode(request.getPassword()))
                .roles(request.getRoles())
                .build();

        users.createUser(user);

        return info;
    }


}
