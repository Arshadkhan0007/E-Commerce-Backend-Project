package com.example.SecurityService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationMessageDto {

    private String username;
    private String email;
    private String provider;

}
