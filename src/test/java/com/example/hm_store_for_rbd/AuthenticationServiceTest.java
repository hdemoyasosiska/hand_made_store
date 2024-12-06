package com.example.hm_store_for_rbd;

import com.example.hm_store.Services.AuthenticationService;
import com.example.hm_store.entity.User;
import com.example.hm_store.repo.ItemRepository;
import com.example.hm_store.repo.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // автоматически инжектит моки (вместо .openMocks(this))
class AuthenticationServiceTest {
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;


    @BeforeEach
    void setUp() {
        //MockitoAnnotations.openMocks(this);
        authenticationService = new AuthenticationService(userRepository);
    }
    @Test
    void loadUserByUsername_UserExists_ReturnsUserDetails() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        UserDetails userDetails = authenticationService.loadUserByUsername(email);
        assertNotNull(userDetails);
        assertEquals(user.getEmail(), userDetails.getUsername());
    }
    @Test
    void loadUserByUsername_UserDoesNotExist_ThrowsUsernameNotFoundException() {
        //AuthenticationService authenticationService = new AuthenticationService(userRepository);
        String email = "nonexistent@example.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.loadUserByUsername(email));
    }
}
