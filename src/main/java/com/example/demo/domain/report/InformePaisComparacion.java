package com.example.demo.domain.report;

import com.example.demo.domain.core.Pais;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class InformePaisComparacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer informe_pais_comparacion_id;

    @ManyToOne
    @JoinColumn(name = "informe_id")
    private Informe informe;

    @ManyToOne
    @JoinColumn(name = "pais_comparacion_id")
    private Pais pais_comparacion;
}