package com.example.demo.domain.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Optional;

public interface PaisRepository extends JpaRepository<Pais, Integer> {
    @Query("SELECT p FROM Pais p WHERE UPPER(p.nombre_pais) = UPPER(:nombre)")
    Optional<Pais> findByNombrePais(@Param("nombre") String nombre);
}
