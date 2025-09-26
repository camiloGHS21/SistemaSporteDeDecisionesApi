package com.example.demo.domain.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
  
    @Query("SELECT r FROM Role r WHERE r.nombre_rol = :nombreRol")
    Optional<Role> findByNombreRol(String nombreRol);

   
}