
package com.example.demo.infrastructure;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.Auth.JwtTokenProvider;
import com.example.demo.domain.user.Role;
import com.example.demo.domain.user.RoleRepository;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OecdDataControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String userToken;

    @BeforeEach
    void setUp() {
        Role userRole = roleRepository.findByNombreRol("ROLE_USER")
                .orElseGet(() -> {
                    Role newUserRole = new Role();
                    newUserRole.setNombre_rol("ROLE_USER");
                    return roleRepository.save(newUserRole);
                });

        User user = new User();
        user.setNombre_usuario("user");
        user.setEmail("user@example.com");
        user.setContrasena_hash(passwordEncoder.encode("password"));
        user.setRol(userRole);
        userRepository.save(user);

        userToken = jwtTokenProvider.generateToken(new org.springframework.security.core.userdetails.User(user.getEmail(), user.getContrasena_hash(), Collections.singletonList(new SimpleGrantedAuthority(user.getRol().getNombre_rol()))));
    }

    @Test
    @WithMockUser
    void getOecdData_whenUserIsAuthenticated_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/oecd-data/MEX/2020")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }
}
