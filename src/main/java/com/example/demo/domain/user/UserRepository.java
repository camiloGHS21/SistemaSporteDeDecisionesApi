package com.example.demo.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.nombre_usuario = :nombre_usuario")
    Optional<User> findByNombreUsuario(@Param("nombre_usuario") String nombre_usuario);


    @Query("SELECT u FROM User u JOIN FETCH u.rol WHERE u.email = :email")
    Optional<User> findByEmailWithRoles(String email);

}
