package com.example.demo.domain.report;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


public interface InformeRepository extends JpaRepository<Informe, Integer> {


    @Query("SELECT i FROM Informe i LEFT JOIN FETCH i.usuario LEFT JOIN FETCH i.pais_principal LEFT JOIN FETCH i.paises_comparacion WHERE i.informe_id = :id")
    Optional<Informe> findByIdWithAllData(Integer id);
}
