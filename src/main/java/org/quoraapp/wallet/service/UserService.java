package org.quoraapp.wallet.service;

import java.util.List;

import org.quoraapp.wallet.entities.User;
import org.quoraapp.wallet.repositories.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;

    public User creeateUser(User user){
        log.info("Creating user: {}", user.getEmail());
        User newUser =  userRepository.save(user);
        log.info("User created with ID: {}", newUser.getId() , (newUser.getId() % 2 + 1));
        return newUser;
    }

    public List<User> getUserByName(String name) {
        log.info("Fetching users with name: {}", name);
        List<User> users = userRepository.getUserByName(name);
        log.info("Found {} users with name: {}", users.size(), name);
        return users;
    }
}
