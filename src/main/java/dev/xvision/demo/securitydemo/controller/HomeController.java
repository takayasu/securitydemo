package dev.xvision.demo.securitydemo.controller;

import dev.xvision.demo.securitydemo.bean.Home;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.stream.Collectors;

@RestController
public class HomeController {

    @GetMapping("/")
    public Home home(){
        Home home = new Home();
        home.setValue("1230");
        return home;
    }

    @GetMapping("/home")
    public Home userHome(){
        Home home = new Home();
        home.setValue("1230");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        home.setUserid(auth.getName());
        home.setRoles(auth.getAuthorities().stream().map(Objects::toString).collect(Collectors.joining(",")));
        return home;
    }

}
