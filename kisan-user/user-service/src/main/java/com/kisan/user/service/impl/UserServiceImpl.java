package com.kisan.user.service.impl;

import com.kisan.user.dto.UserDTO;
import com.kisan.user.entity.User;
import com.kisan.user.repository.UserRepository;
import com.kisan.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())){
            throw new RuntimeException("User already registered with this number");
        }

        User user = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .villageName(userDTO.getVillageName())
                .district(userDTO.getDistrict())
                .role(userDTO.getRole())
                .build();

        User savedUser = userRepository.save(user);
        return mapToDTO(savedUser);
    }

    @Override
    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: "+ userId));
        return mapToDTO(user);
    }

    @Override
    public UserDTO getUserByPhoneNumber(String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with phone number: "+ phoneNumber));
        return mapToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /**
     * method for map user into DTO.
     * Located in the UserService class.
     */
    private UserDTO mapToDTO(User user){
        return UserDTO.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .villageName(user.getVillageName())
                .district(user.getDistrict())
                .role(user.getRole())
                .build();
    }
}
