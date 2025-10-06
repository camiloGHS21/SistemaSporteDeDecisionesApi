package com.example.demo.domain.report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.example.demo.domain.core.Pais;
import com.example.demo.domain.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Informe {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

  
    private LocalDateTime fecha_generacion;

    private String nombre_informe;

 
    @ManyToOne
    @JoinColumn(name = "pais_principal_id")
    private Pais pais_principal;


    @OneToMany(mappedBy = "informe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<InformePaisComparacion> paises_comparacion;

    @ElementCollection
    private List<String> indicadores;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Informe informe = (Informe) o;
        return Objects.equals(id, informe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}