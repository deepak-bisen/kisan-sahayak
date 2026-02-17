package com.kisan.user.service;

import com.kisan.user.dto.UserDTO;

import java.util.List;

   /**
     * Service interface for User business logic.
     * Located in the 'user-service' module for abstraction.
     */
    public interface UserService {
        UserDTO registerUser(UserDTO userDTO);
        UserDTO getUserById(String userId);
        UserDTO getUserByPhoneNumber(String phoneNumber);
        List<UserDTO> getAllUsers();
    }