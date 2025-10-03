package com.example.demo.domain.core;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.demo.domain.report.Informe;
import com.example.demo.domain.report.InformePaisComparacion;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Data;


@Data
@Entity
public class Pais {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pais_id;

    private String nombre_pais;

    private String codigo_iso;

    @JsonIgnore
    @OneToMany(mappedBy = "pais")
    private Set<DatoIndicador> datos;

    @JsonIgnore
    @OneToMany(mappedBy = "pais_comparacion")
    private Set<InformePaisComparacion> informes_comparacion;

    @JsonIgnore
    @OneToMany(mappedBy = "pais_principal")
    private Set<Informe> informes_principal;
}