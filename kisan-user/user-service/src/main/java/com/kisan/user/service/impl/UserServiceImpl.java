package com.kisan.user.service.impl;

import com.kisan.user.dto.LoginRequestDTO;
import com.kisan.user.dto.UserDTO;
import com.kisan.user.entity.User;
import com.kisan.user.repository.UserRepository;
import com.kisan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())){
            throw new RuntimeException("User already registered with this number");
        }

        User user = User.builder()
                .fullName(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(passwordEncoder.encode(userDTO.getPassword())) // Encrypt!
                .villageName(userDTO.getVillageName())
                .district(userDTO.getDistrict())
                .role(userDTO.getRole())
                .build();

        return mapToDTO(userRepository.save(user));
    }

    @Override
    public UserDTO login(LoginRequestDTO request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }
        return mapToDTO(user);
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
                .orElseThrow(() -> new RuntimeException("User not found with phone: "+ phoneNumber));
        return mapToDTO(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public void deleteUserByUserId(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserDTO updateUser(String userId, UserDTO userDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found with this id: "+ userId));

        //Partial Update: Only fields if they are provided in the DTO
        if (userDTO.getFullName() != null) existingUser.setFullName(userDTO.getFullName());
        if (userDTO.getVillageName() != null) existingUser.setVillageName(userDTO.getVillageName());
        if (userDTO.getDistrict() != null) existingUser.setDistrict(userDTO.getDistrict());
        if (userDTO.getState() != null) existingUser.setState(userDTO.getState());
        if (userDTO.getRole() != null) existingUser.setRole(userDTO.getRole());

        // Handle phone number update (careful with uniqueness)
        if (userDTO.getPhoneNumber() != null && !userDTO.getPhoneNumber().equals(existingUser.getPhoneNumber())){
            if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())){
                throw new RuntimeException("Phone number already in use by another account");
            }
            existingUser.setPhoneNumber(userDTO.getPhoneNumber());
        }

        //handle password update if provided
        if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()){
            existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword())) ;
        }

        return mapToDTO(userRepository.save(existingUser));
    }

    /**
     *@Override
     *public void deleteUserByPhone(String phoneNumber) {
     * userRepository.deleteByPhoneNumber(phoneNumber);
     * }
     */

    private UserDTO mapToDTO(User user){
        return UserDTO.builder()
                .userId(user.getUserId())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .villageName(user.getVillageName())
                .district(user.getDistrict())
                .state(user.getState())
                .role(user.getRole())
                .build();
    }
}