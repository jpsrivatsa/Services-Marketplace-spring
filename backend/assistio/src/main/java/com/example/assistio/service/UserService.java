package com.example.assistio.service;
import org.springframework.stereotype.Service;
import com.example.assistio.model.*;
import com.example.assistio.repository.UserRepository;
@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    public Role getRoleOfUser(String username){
        return userRepository.findRoleByUsername(username);
    }
    public User getUser(String username){
        userRepository.findByUsername(username).get().setPassword("***");
        return userRepository.findByUsername(username).get();
    }
}
