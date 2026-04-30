package com.example.SecurityService.service;

import com.example.SecurityService.dto.LoginRequestDto;
import com.example.SecurityService.dto.LoginResponseDto;
import com.example.SecurityService.dto.RegistrationRequestDto;
import com.example.SecurityService.entity.User;
import com.example.SecurityService.enums.ProviderType;
import com.example.SecurityService.enums.TokenType;
import com.example.SecurityService.exception.AccountConflictException;
import com.example.SecurityService.exception.ResourceNotFoundException;
import com.example.SecurityService.repository.RoleRepository;
import com.example.SecurityService.repository.UserRepository;
import com.example.SecurityService.security.AuthUtil;
import com.example.SecurityService.security.CustomUserDetailsService;
import com.example.SecurityService.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthUtil authUtil;
    private final KafkaTemplate kafkaTemplate;
    private final String registrationTopicName;
    private final String loginTopicName;

    public AuthenticationService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            CustomUserDetailsService customUserDetailsService,
            AuthUtil authUtil,
            KafkaTemplate kafkaTemplate,
            @Value("${spring.registration-topic-name}") String registrationTopicName,
            @Value("${spring.login-topic-name}") String loginTopicName) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.customUserDetailsService = customUserDetailsService;
        this.authUtil = authUtil;
        this.kafkaTemplate = kafkaTemplate;
        this.registrationTopicName = registrationTopicName;
        this.loginTopicName = loginTopicName;
    }

    public void register(RegistrationRequestDto requestDto) {
        registerInternal(requestDto, ProviderType.EMAIL, null);
    }

    private User registerInternal(RegistrationRequestDto requestDto, ProviderType providerType, String providerId) {

        if(userRepository.findByUsername(requestDto.getUsername()).isPresent())
            throw new AccountConflictException("User has already registered with this email");

        User user = User.builder()
                .username(requestDto.getUsername())
                .providerType(providerType)
                .providerId(providerId)
                .roleSet(requestDto.getRoles().stream()
                        .map(roleName -> roleRepository.findByRoleName(roleName)
                                .orElseThrow(() -> new ResourceNotFoundException(roleName + " role does not exist")))
                        .collect(Collectors.toSet()))
                .build();

        if(providerType == ProviderType.EMAIL) {
            user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        }

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(registrationTopicName, user.getUserId(), user.getUsername());

        future.whenComplete((result, ex) -> {
            if(ex == null) {
                System.out.printf("Message has been published successfully | Data: %s | Partition: %s | Offset: %s%n", result.getProducerRecord().value(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                System.out.printf("Unable to send message, Reason: %s%n", ex.getMessage());
            }
        });

        return userRepository.save(user);
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUsername(), requestDto.getPassword())
        );
        UserDetails userDetails = (UserDetails) authenticate.getPrincipal();

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(loginTopicName, requestDto.getUsername(), requestDto.getUsername());

        future.whenComplete((result, ex) -> {
            if(ex == null) {
                System.out.printf("Message has been published successfully | Data: %s | Partition: %s | Offset: %s%n", result.getProducerRecord().value(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                System.out.printf("Unable to send message, Reason: %s%n", ex.getMessage());
            }
        });

        return new LoginResponseDto(
                jwtUtil.generateAccessToken(userDetails),
                jwtUtil.generateRefreshToken(userDetails)
        );
    }

    @Transactional
    public LoginResponseDto loginWithOAuthProvider(OAuth2User oAuth2User, String registrationId) {
        ProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);
        String oAuthEmail = oAuth2User.getAttribute("email");

        User userByProvider = userRepository.findByProviderTypeAndProviderId(providerType, providerId).orElse(null);
        User userByEmail = userRepository.findByUsername(oAuthEmail).orElse(null);

        User finalUser;

        if(userByProvider == null) {
            finalUser = handleNewOAuthUser(oAuth2User, registrationId, providerType, providerId, userByEmail);
        } else {
            finalUser = handleExistingOAuthUser(userByProvider, oAuthEmail, userByEmail);
        }

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(finalUser.getUsername());

        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(loginTopicName, userDetails.getUsername(), userDetails.getUsername());

        future.whenComplete((result, ex) -> {
            if(ex == null) {
                System.out.printf("Message has been published successfully | Data: %s | Partition: %s | Offset: %s%n", result.getProducerRecord().value(), result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
            } else {
                System.out.printf("Unable to send message, Reason: %s%n", ex.getMessage());
            }
        });

        return new LoginResponseDto(
                jwtUtil.generateAccessToken(userDetails),
                jwtUtil.generateRefreshToken(userDetails)
        );

    }

    private User handleNewOAuthUser(OAuth2User oAuth2User, String registrationId, ProviderType providerType, String providerId, User userByEmail) {
        if (userByEmail != null) throw new AccountConflictException("Email is already registered via: " + userByEmail.getProviderType());

        String username= authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
        RegistrationRequestDto requestDto = new RegistrationRequestDto(username, null, Set.of("ROLE_USER"));
        return registerInternal(requestDto, providerType, providerId);
    }

    private User handleExistingOAuthUser(User userByProvider, String oAuthEmail, User userByEmail) {
        if(oAuthEmail == null || oAuthEmail.isBlank() || oAuthEmail.equals(userByProvider.getUsername())) {
            return userByProvider;
        }

        if(userByEmail != null && userByEmail.getUserId() != userByProvider.getUserId()) {
            throw new AccountConflictException("Your provider email changed, but the new email is already in use.");
        }

        userByProvider.setUsername(oAuthEmail);
        return userRepository.save(userByProvider);
    }

    public String refresh(String token) {
        if (jwtUtil.extractTokenType(token) != TokenType.REFRESH && jwtUtil.isTokenExpired(token)) {
            throw new IllegalArgumentException("Provided token is not a refresh token");
        }
        return jwtUtil.generateAccessToken(
                customUserDetailsService.loadUserByUsername(
                        jwtUtil.extractUsername(token)));
    }

}
