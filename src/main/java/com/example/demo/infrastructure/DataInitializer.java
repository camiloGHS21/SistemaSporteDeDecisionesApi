package com.example.demo.infrastructure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.domain.user.Role;
import com.example.demo.domain.user.RoleRepository;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@ghs21.online").isEmpty()) {
            Role adminRole = roleRepository.findByNombreRol("ROLE_ADMIN").orElseGet(() -> {
                Role newRole = new Role();
                newRole.setNombre_rol("ROLE_ADMIN");
                return roleRepository.save(newRole);
            });

            User admin = new User();
            admin.setNombre_usuario("admin");
            admin.setEmail("admin@ghs21.online");
            admin.setContrasena_hash(passwordEncoder.encode("admin1234"));
            admin.setRol(adminRole);
            userRepository.save(admin);
        }
    }
}
