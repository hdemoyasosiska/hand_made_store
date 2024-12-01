package com.example.hm_store.Services;

import com.example.hm_store.entity.User;
import com.example.hm_store.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }

    public void save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
    }

    public void createUser(String name, String email, String password){
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }

    public User getById(int id){
        return userRepository.getById(id);
    }

    public boolean update(User user, int id){
        if (userRepository.existsById(id)) {
            user.setId(id);
            userRepository.save(user);
            return true;
        } else {
            return false;
        }
    }

    public boolean deleteById(int id){
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}

