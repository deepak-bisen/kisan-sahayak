package com.kisan.user.service;

import com.kisan.user.dto.LoginRequestDTO;
import com.kisan.user.dto.UserDTO;

import java.util.List;

   /**
     * Service interface for User business logic.
     * Located in the 'user-service' module for abstraction.
     */
    public interface UserService {
        UserDTO registerUser(UserDTO userDTO);
        UserDTO login(LoginRequestDTO loginRequest); // New login method
        UserDTO getUserById(String userId);
        UserDTO getUserByPhoneNumber(String phoneNumber);
        List<UserDTO> getAllUsers();

//        void deleteUserByPhone(String phoneNumber);
        void deleteUserByUserId(String userId);

       /**
        * Updates an existing user's profile information.
        * @param userId The ID of the user to update.
        * @param userDTO The data containing updated fields.
        * @return The updated UserDTO.
        */
       UserDTO updateUser(String userId, UserDTO userDTO);
    }