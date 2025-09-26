package com.example.demo.domain.external;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Entity
public class ExternalData {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

 
    @NotNull(message = "El país no puede ser nulo.")
    @NotEmpty(message = "El país no puede estar vacío.")
    private String country;

   
    @NotNull(message = "El año no puede ser nulo.")
    @NotEmpty(message = "El año no puede estar vacío.")
    @Column(name = "data_year")
    private String dataYear;

   
    @NotNull(message = "El indicador no puede ser nulo.")
    @NotEmpty(message = "El indicador no puede estar vacío.")
    private String indicator;

    @NotNull(message = "El valor no puede ser nulo.")
    @NotEmpty(message = "El valor no puede estar vacío.")
    @Column(name = "data_value")
    private String dataValue;
}