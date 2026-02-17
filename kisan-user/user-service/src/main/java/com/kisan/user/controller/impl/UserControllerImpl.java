package com.kisan.user.controller.impl;

import com.kisan.user.controller.UserController;
import com.kisan.user.dto.UserDTO;
import com.kisan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;

    @PostMapping("/register")
    @Override
    public ResponseEntity<UserDTO> register(@RequestBody UserDTO userDTO){
        return new ResponseEntity<>(userService.registerUser(userDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{userId}")
    @Override
    public ResponseEntity<UserDTO> getUser(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/phone/{phoneNumber}")
    @Override
    public ResponseEntity<UserDTO> getUserByPhone(@PathVariable String phoneNumber) {
        return ResponseEntity.ok(userService.getUserByPhoneNumber(phoneNumber));
    }

    @GetMapping
    @Override
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
