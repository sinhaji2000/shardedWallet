package org.quoraapp.wallet.controllers;

import org.quoraapp.wallet.entities.User;
import org.quoraapp.wallet.service.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {
    
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        
        User newUser = userService.creeateUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }
    
}
