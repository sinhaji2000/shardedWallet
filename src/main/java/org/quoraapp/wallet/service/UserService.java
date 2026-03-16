package org.quoraapp.wallet.service;

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
}
