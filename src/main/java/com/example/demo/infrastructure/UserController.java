package com.example.demo.infrastructure;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.application.Auth.JwtTokenProvider;
import com.example.demo.domain.user.User;
import com.example.demo.domain.user.UserAlreadyExistsException;
import com.example.demo.domain.user.UserService;
import com.example.demo.infrastructure.Auth.LoginRequest;
import com.example.demo.infrastructure.Auth.LoginResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Operation(
        summary = "Registrar un nuevo usuario",
        description = "Crea una nueva cuenta de usuario en el sistema.",
        responses = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado con éxito"),
            @ApiResponse(responseCode = "409", description = "El usuario ya existe")
        }
    )
    @PostMapping("/users/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerUser(user);
            return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        }
    }

    @Operation(
        summary = "Iniciar sesión de usuario",
        description = "Autentica a un usuario y devuelve un token JWT si las credenciales son correctas.",
        responses = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, devuelve token JWT"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
        }
    )
    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());
        return ResponseEntity.ok(new LoginResponse(jwt));
    }

    @Operation(
        summary = "Validar un token JWT",
        description = "Verifica si un token JWT proporcionado en la cabecera de autorización es válido.",
        responses = {
            @ApiResponse(responseCode = "200", description = "El token es válido"),
            @ApiResponse(responseCode = "401", description = "El token es inválido o ha expirado")
        }
    )
    @PostMapping("/auth/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        String jwt = token.substring(7); // Remove "Bearer " prefix
        String username = jwtTokenProvider.extractUsername(jwt);
        UserDetails userDetails = userService.loadUserByUsername(username);

        if (jwtTokenProvider.isTokenValid(jwt, userDetails)) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
        }
    }
}
