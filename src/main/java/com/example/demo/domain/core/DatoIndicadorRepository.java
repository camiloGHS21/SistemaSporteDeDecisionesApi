package com.example.demo.domain.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DatoIndicadorRepository extends JpaRepository<DatoIndicador, Integer> {

    @Query("SELECT d FROM DatoIndicador d JOIN FETCH d.pais WHERE d.anio = :anio")
    List<DatoIndicador> findByAnioWithPais(Integer anio);

    @Query("SELECT d FROM DatoIndicador d WHERE UPPER(d.pais.nombre_pais) = UPPER(:nombrePais)")
    List<DatoIndicador> findByPais_NombrePaisIgnoreCase(@Param("nombrePais") String nombrePais);

    boolean existsByPaisAndTipoIndicadorAndAnio(Pais pais, String tipoIndicador, Integer anio);

    @Query("SELECT DISTINCT d.tipoIndicador FROM DatoIndicador d ORDER BY d.tipoIndicador")
    List<String> findDistinctTipoIndicador();

    @Query("SELECT d FROM DatoIndicador d WHERE UPPER(d.pais.nombre_pais) IN :paises AND UPPER(d.tipoIndicador) IN :indicadores")
    List<DatoIndicador> findByPaisesAndIndicadores(@Param("paises") List<String> paises, @Param("indicadores") List<String> indicadores);
}
