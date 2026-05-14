package com.example.SecurityService.security;

import com.example.SecurityService.entity.User;
import com.example.SecurityService.enums.ProviderType;
import com.example.SecurityService.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with username: " + username + " does not exist!"));
        Set<SimpleGrantedAuthority> authorities = user.getRoleSet().stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());
        String password = user.getProviderType() == ProviderType.EMAIL ? user.getPassword() : "OAUTH_USER";

        log.info("USERNAME: {} | PASSWORD: {} | AUTHORITIES: {}", username, password, authorities);

        return new org.springframework.security.core.userdetails.User(
                username,
                password,
                authorities
        );
    }
}
