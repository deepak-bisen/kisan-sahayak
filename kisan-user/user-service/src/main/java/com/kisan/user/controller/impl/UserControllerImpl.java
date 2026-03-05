package com.kisan.user.controller.impl;

import com.kisan.user.controller.UserController;
import com.kisan.user.dto.LoginRequestDTO;
import com.kisan.user.dto.UserDTO;
import com.kisan.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final UserService userService;


    @PostMapping("/register")
    @Transactional
    @Override
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserDTO userDTO){
        return new ResponseEntity<>(userService.registerUser(userDTO), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(userService.login(loginRequest));
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

    /**
     * Deletes a user from the system by their unique ID.
     * @param userId The UUID of the user to delete.
     */
    @DeleteMapping("delete/{userId}")
    @Override
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable String userId) {
        userService.deleteUserByUserId(userId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/update/{userId}")
    @Override
    @Transactional
    public ResponseEntity<UserDTO> updateUser(@PathVariable String userId,@RequestBody UserDTO userDTO) {

        return ResponseEntity.ok(userService.updateUser(userId,userDTO));
    }

    /*
    @DeleteMapping("delete/{phoneNumber}")
    @Override
    @Transactional
    public ResponseEntity<Void> deleteUser(@PathVariable String phoneNumber) {
        userService.deleteUserByPhone(phoneNumber);
        return ResponseEntity.noContent().build();
    }
    */
}
