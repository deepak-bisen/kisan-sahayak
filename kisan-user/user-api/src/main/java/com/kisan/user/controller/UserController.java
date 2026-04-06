package com.kisan.user.controller;

import com.kisan.user.dto.AuthResponseDTO;
import com.kisan.user.dto.LoginRequestDTO;
import com.kisan.user.dto.UserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User operations.
 */
@RequestMapping("/api/users")
public interface UserController {
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody UserDTO userDTO);

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@RequestBody LoginRequestDTO loginRequest);

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable String userId);

    @GetMapping("/phone/{phoneNumber}")
    public ResponseEntity<UserDTO> getUserByPhone(@PathVariable String phoneNumber);

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers();

    /**
     * Deletes a user from the system by their unique ID.
     * @param userId The UUID of the user to delete.
     */
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable String userId);

    @DeleteMapping("/delete/{phone}")
    public ResponseEntity<Void> deleteUserByPhone(@PathVariable String phoneNumber);


    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String userId,@RequestBody UserDTO userDTO);
}