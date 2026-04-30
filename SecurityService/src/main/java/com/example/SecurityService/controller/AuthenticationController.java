package com.example.SecurityService.controller;

import com.example.SecurityService.dto.LoginRequestDto;
import com.example.SecurityService.dto.LoginResponseDto;
import com.example.SecurityService.dto.RegistrationRequestDto;
import com.example.SecurityService.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    public AuthenticationController(AuthenticationService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegistrationRequestDto requestDto) {
        service.register(requestDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        return new ResponseEntity<>(service.login(requestDto), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestParam String token) {
        return new ResponseEntity<>(service.refresh(token), HttpStatus.OK);
    }
}
