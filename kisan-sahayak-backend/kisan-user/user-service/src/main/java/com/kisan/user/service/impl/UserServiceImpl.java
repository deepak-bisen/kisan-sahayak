package com.kisan.user.service.impl;

import com.kisan.user.security.JwtUtil;
import com.kisan.user.dto.AuthResponseDTO;
import com.kisan.user.dto.LoginRequestDTO;
import com.kisan.user.dto.UserDTO;
import com.kisan.user.entity.User;
import com.kisan.user.repository.UserRepository;
import com.kisan.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final RestClient restClient;

    @Override
    @Transactional
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByPhoneNumber(userDTO.getPhoneNumber())){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already registered with this number");
        }

        log.info("user registration started...");
        String roles = resolveRoles(userDTO.getRoles());

        User user = User.builder()
                .name(userDTO.getFullName())
                .phoneNumber(userDTO.getPhoneNumber())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .villageName(userDTO.getVillageName())
                .district(userDTO.getDistrict())
                .roles(roles)
                .build();

        User registeredUser = userRepository.save(user);
        return mapToDTO(registeredUser);
    }

    @Override
    @Transactional
    public AuthResponseDTO loginUser(LoginRequestDTO request) {
        String phoneNumber = request.getPhoneNumber();
        log.info("Login request received for phoneNumber={}", maskPhoneNumber(phoneNumber));

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> {
                    log.warn("Login failed: user not found for phoneNumber={}", maskPhoneNumber(phoneNumber));
                    return new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid phone number or password.");
                });

        // Compare raw password with hashed database password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed: invalid password for phoneNumber={}", maskPhoneNumber(phoneNumber));
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid phone number or password.");
        }

        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getId(), user.getRoles());

        UserDTO userDTO = mapToDTO(user);
        log.info("Login successful for userId={} roles={} phoneNumber={}", user.getId(), user.getRoles(), maskPhoneNumber(user.getPhoneNumber()));

        //return both the token and the user details
        return AuthResponseDTO.builder()
                .token(token)
                .user(userDTO)
                .build();
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
    public String refreshToken(String token) {
        return jwtUtil.refreshToken(token);
    }

    @Override
    @Transactional
    public void deleteUserByUserId(String userId) {
        try {
            restClient.delete()
                    .uri("/api/marketplace/equipment/owner/{ownerId}", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to cascade-delete equipment for userId={}: {}", userId, e.getMessage());
        }

        try {
            restClient.delete()
                    .uri("/api/marketplace/bookings/renter/{renterId}", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to cascade-delete bookings for userId={}: {}", userId, e.getMessage());
        }

        try {
            restClient.delete()
                    .uri("/api/marketplace/notifications/user/{userId}", userId)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to cascade-delete notifications for userId={}: {}", userId, e.getMessage());
        }

        userRepository.deleteById(userId);
        log.info("user deleted successfully with id: " + userId);
    }


    @Override
    @Transactional
    public UserDTO updateUser(String userId, UserDTO userDTO) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found with this id: "+ userId));

        if (userDTO.getFullName() != null) existingUser.setName(userDTO.getFullName());
        if (userDTO.getVillageName() != null) existingUser.setVillageName(userDTO.getVillageName());
        if (userDTO.getDistrict() != null) existingUser.setDistrict(userDTO.getDistrict());
        if (userDTO.getState() != null) existingUser.setState(userDTO.getState());
        if (userDTO.getRoles() != null) existingUser.setRoles(resolveRoles(userDTO.getRoles()));

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

    @Override
    public void deleteUserByPhone(String phoneNumber) {
        userRepository.deleteByPhoneNumber(phoneNumber);
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() <= 4) {
            return "***";
        }
        return phoneNumber.substring(0, 2) + "***" + phoneNumber.substring(phoneNumber.length() - 2);
    }

    private UserDTO mapToDTO(User user){
        return UserDTO.builder()
                .userId(user.getId())
                .fullName(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .villageName(user.getVillageName())
                .district(user.getDistrict())
                .state(user.getState() != null ? user.getState() : "")
                .roles(user.getRoleList())
                .build();
    }

    private String resolveRoles(List<String> roles) {
        if (roles == null || roles.isEmpty()) return "FARMER";
        return String.join(",", roles);
    }
}