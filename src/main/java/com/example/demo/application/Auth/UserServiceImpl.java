
package com.example.demo.application.Auth;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.domain.user.Role;
import com.example.demo.domain.user.RoleRepository;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserAlreadyExistsException;
import com.example.demo.domain.user.UserRepository;
import com.example.demo.domain.user.UserService;


@Service
public class UserServiceImpl implements UserService, UserDetailsService {

 
    private final UserRepository userRepository;
  
    private final PasswordEncoder passwordEncoder;
   
    private final RoleRepository roleRepository;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

 
    @Override
    public User registerUser(User user) throws UserAlreadyExistsException {
        if (userRepository.findByEmail(user.getEmail()).isPresent() || userRepository.findByNombreUsuario(user.getNombre_usuario()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + user.getEmail() + " or username " + user.getNombre_usuario() + " already exists.");
        }
        user.setContrasena_hash(passwordEncoder.encode(user.getContrasena_hash()));
        Role userRole = roleRepository.findByNombreRol("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        user.setRol(userRole);
        return userRepository.save(user);
    }

   
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailWithRoles(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getContrasena_hash(),
                java.util.Collections.singletonList(new SimpleGrantedAuthority(user.getRol().getNombre_rol())));
    }
}