
package com.example.demo.infrastructure;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.application.auth.JwtTokenProvider;
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
        Role userRole = new Role();
        userRole.setNombre_rol("ROLE_USER");
        roleRepository.save(userRole);

        User user = new User();
        user.setNombre_usuario("user");
        user.setEmail("user@example.com");
        user.setContrasena_hash(passwordEncoder.encode("password"));
        user.setRol(userRole);
        userRepository.save(user);

        userToken = jwtTokenProvider.generateToken(new org.springframework.security.core.userdetails.User(user.getEmail(), user.getContrasena_hash(), Collections.singletonList(new SimpleGrantedAuthority(user.getRol().getNombre_rol()))));
    }

    @Test
    void getOecdData_whenUserIsAuthenticated_shouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/oecd-data/MEX/2020")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }
}
