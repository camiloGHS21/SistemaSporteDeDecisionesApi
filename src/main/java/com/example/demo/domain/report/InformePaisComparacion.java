package com.example.demo.domain.report;

import com.example.demo.domain.core.Pais;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InformePaisComparacion that = (InformePaisComparacion) o;
        return Objects.equals(informe_pais_comparacion_id, that.informe_pais_comparacion_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(informe_pais_comparacion_id);
    }
}