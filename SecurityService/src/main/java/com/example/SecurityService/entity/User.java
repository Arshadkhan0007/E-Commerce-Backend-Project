package com.example.SecurityService.entity;

import com.example.SecurityService.enums.ProviderType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "E_COM_USER")
public class User {

    @Id
    @SequenceGenerator(name = "userSeq", sequenceName = "E_COM_USER_SEQ")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "userSeq")
    private Integer userId;
    @Column(unique = true)
    private String username;
    private String password;
    private String providerId;
    @Enumerated(EnumType.STRING)
    private ProviderType providerType;
    @ManyToMany
    @JoinTable(
            name = "E_COM_USER_ROLE",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roleSet;

}
