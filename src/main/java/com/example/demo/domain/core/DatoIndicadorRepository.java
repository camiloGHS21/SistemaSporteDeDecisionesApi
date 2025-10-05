package com.example.demo.domain.core;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.domain.user.User;

public interface DatoIndicadorRepository extends JpaRepository<DatoIndicador, Integer> {

    @Query("SELECT d FROM DatoIndicador d JOIN FETCH d.pais WHERE d.anio = :anio")
    List<DatoIndicador> findByAnioWithPais(Integer anio);

    @Query("SELECT d FROM DatoIndicador d WHERE d.user = :user AND UPPER(d.pais.nombre_pais) = UPPER(:nombrePais)")
    List<DatoIndicador> findByUserAndPais_Nombre_paisIgnoreCase(@Param("user") User user, @Param("nombrePais") String nombrePais);

    boolean existsByPaisAndTipoIndicadorAndAnio(Pais pais, String tipoIndicador, Integer anio);

    @Query("SELECT DISTINCT d.tipoIndicador FROM DatoIndicador d where d.user = :user ORDER BY d.tipoIndicador")
    List<String> findDistinctTipoIndicadorByUser(@Param("user") User user);

    @Query("SELECT d FROM DatoIndicador d WHERE d.user = :user AND UPPER(d.pais.nombre_pais) IN :paises AND UPPER(d.tipoIndicador) IN :indicadores")
    List<DatoIndicador> findByUserAndPaisesAndIndicadores(@Param("user") User user, @Param("paises") List<String> paises, @Param("indicadores") List<String> indicadores);

    List<DatoIndicador> findByUser(User user);

    List<DatoIndicador> findByUserAndTipoIndicador(User user, String tipoIndicador);

}
