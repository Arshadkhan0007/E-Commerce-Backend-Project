package com.example.SecurityService.repository;

import com.example.SecurityService.entity.User;
import com.example.SecurityService.enums.ProviderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsername(String username);
    Optional<User> findByProviderTypeAndProviderId(ProviderType providerType, String providerId);

}
