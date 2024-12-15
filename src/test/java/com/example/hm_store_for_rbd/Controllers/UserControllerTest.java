package com.example.hm_store_for_rbd.Controllers;


import com.example.hm_store.Controllers.ItemController;
import com.example.hm_store.Controllers.UserController;
import com.example.hm_store.Services.ItemService;
import com.example.hm_store.Services.UserService;
import com.example.hm_store.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void createUser_new() throws Exception {
        String name = "Test Item";
        String email = "email@gmail.com";
        String password = "Red";

        when(userService.existsByEmail(email)).thenReturn(false);
        mockMvc.perform(post("/regist")
                        .param("name", name)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/home"));

        verify(userService, times(1))
                .createUser(any(String.class), any(String.class), any(String.class));
    }

    @Test
    void createUser_exists() throws Exception {
        String name = "Test Item";
        String email = "old_email@gmail.com";
        String password = "Red";

        when(userService.existsByEmail(email)).thenReturn(true);
        mockMvc.perform(post("/regist")
                        .param("name", name)
                        .param("email", email)
                        .param("password", password))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/regist"));

        verify(userService, times(0))
                .createUser(any(String.class), any(String.class), any(String.class));
    }
}
