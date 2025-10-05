package com.example.demo.domain.core;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import com.example.demo.domain.user.User;
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
    @JoinColumn(name = "usuario_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "pais_id")
    private Pais pais;

    private String tipoIndicador;

    private float valor;

    private Integer anio;

    private String fuente;
}