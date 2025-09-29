package com.example.demo.domain.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DatoIndicadorRepository extends JpaRepository<DatoIndicador, Integer> {

    @Query("SELECT d FROM DatoIndicador d JOIN FETCH d.pais WHERE d.anio = :anio")
    List<DatoIndicador> findByAnioWithPais(Integer anio);

}
