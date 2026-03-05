package com.kisan.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequestDTO {

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]{1}[0-9]{9}$", message = "Invalid phone number format")
    private String phoneNumber;

    @NotBlank(message = "Password is required")
    private String password;

}