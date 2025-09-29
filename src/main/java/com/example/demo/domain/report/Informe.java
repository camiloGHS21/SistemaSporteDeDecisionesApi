package com.example.demo.domain.report;

import java.time.LocalDateTime;
import java.util.Set;

import com.example.demo.domain.core.Pais;
import com.example.demo.domain.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Informe {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer informe_id;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

  
    private LocalDateTime fecha_generacion;

    private String nombre_informe;

 
    @ManyToOne
    @JoinColumn(name = "pais_principal_id")
    private Pais pais_principal;


    @OneToMany(mappedBy = "informe")
    private Set<InformePaisComparacion> paises_comparacion;
}