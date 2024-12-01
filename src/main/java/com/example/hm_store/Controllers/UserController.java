package com.example.hm_store.Controllers;

import com.example.hm_store.Services.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(value = "/regist")
    public void createUser(HttpServletResponse response, @RequestParam("name") String name,
                   @RequestParam("email") String email, @RequestParam("password") String password,
                           HttpSession session) throws IOException {
        if (userService.existsByEmail(email)) {
            response.sendRedirect("/regist");
            return;
        }

        userService.createUser(name, email, password);
        response.sendRedirect("/home");
    }



}


