package com.example.hm_store_for_rbd.Services;

import com.example.hm_store.Services.MyUserDetails;
import com.example.hm_store.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyUserDetailsTest {

    @Test
    void testGetAuthorities() {
        // Данные
        User mockUser = new User();
        mockUser.setRole("ROLE_ADMIN, ROLE_USER");

        MyUserDetails userDetails = new MyUserDetails(mockUser);

        // Выполняем метод
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        // Ожидаемый результат
        List<GrantedAuthority> expectedAuthorities = List.of(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
        );

        // Проверяем, что результат соответствует ожиданиям
        assertNotNull(authorities, "Authorities should not be null");
        assertEquals(expectedAuthorities.size(), authorities.size(), "Authorities size mismatch");
        assertTrue(authorities.containsAll(expectedAuthorities), "Authorities do not match expected values");
    }
}

