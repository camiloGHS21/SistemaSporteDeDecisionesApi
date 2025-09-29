package com.example.demo.domain.core;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class DatoIndicador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer dato_id;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;

    private String tipo_indicador;

    private float valor;

    private Integer anio;

    private String fuente;
}