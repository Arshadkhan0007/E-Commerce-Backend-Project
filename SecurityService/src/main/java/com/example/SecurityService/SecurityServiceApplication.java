package com.example.SecurityService;

import com.example.SecurityService.entity.Role;
import com.example.SecurityService.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class SecurityServiceApplication implements CommandLineRunner {

	private final RoleRepository roleRepository;

    public SecurityServiceApplication(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(SecurityServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		roleRepository.saveAll(List.of(
				new Role(1, "ROLE_USER"),
				new Role(2, "ROLE_ADMIN")
		));
	}
}
