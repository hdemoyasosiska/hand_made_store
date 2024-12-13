package com.example.hm_store_for_rbd;

import com.example.hm_store.Services.OrderService;
import com.example.hm_store.Services.UserService;
import com.example.hm_store.entity.Order;
import com.example.hm_store.entity.User;
import com.example.hm_store.repo.ItemRepository;
import com.example.hm_store.repo.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)

public class UserServiceTest {
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void getById_found() {
        int id = 1;
        User user = new User();
        user.setId(id);
        when(userRepository.getReferenceById(id)).thenReturn(user);
        assertEquals(userService.getById(id), user);
    }

    @Test
    void getById_not_found() {
        int id = -1;
        when(userRepository.getReferenceById(id)).thenThrow(EntityNotFoundException.class);
        assertThrows(EntityNotFoundException.class, () -> userService.getById(id));
    }

    @Test
    void createUser_successfully() {
        userService.createUser(null, null, null);
        verify(userRepository, times(1)).save(any(User.class));
    }

}
