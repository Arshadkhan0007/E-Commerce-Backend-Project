package com.example.SecurityService.security;

import com.example.SecurityService.enums.ProviderType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthUtil {

    public ProviderType getProviderTypeFromRegistrationId(String registrationId) {
        return switch (registrationId.toLowerCase()) {
            case "google" -> ProviderType.GOOGLE;
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId.toLowerCase());
        };
    }

    public String determineProviderIdFromOAuth2User(OAuth2User oAuth2User, String registrationId) {
        String providerId = switch (registrationId.toLowerCase()) {
            case "google" -> oAuth2User.getAttribute("sub");
            default -> throw new IllegalArgumentException("Unsupported OAuth2 provider: " + registrationId.toLowerCase());
        };

        if(providerId == null || providerId.isBlank()) {
            throw new IllegalArgumentException("Unable to determine providerId for OAuth2 login");
        }

        return providerId;
    }

    public String determineUsernameFromOAuth2User(OAuth2User oAuth2User) {
        return oAuth2User.getAttribute("name");
    }

    public String determineEmailFromOAuth2User(OAuth2User oAuth2User, String registrationId, String providerId) {
        String email = oAuth2User.getAttribute("email");
        if(email != null && !email.isBlank()) {
            return email;
        }
        return switch (registrationId.toLowerCase()) {
            case "google" -> oAuth2User.getAttribute("sub");
            default -> providerId;
        };
    }

}
