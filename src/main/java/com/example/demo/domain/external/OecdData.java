package com.example.demo.domain.external;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OecdData {

 
    @NotNull(message = "El ID no puede ser nulo.")
    @NotEmpty(message = "El ID no puede estar vacío.")
    private String id;


    @NotNull(message = "El nombre no puede ser nulo.")
    @NotEmpty(message = "El nombre no puede estar vacío.")
    private String name;

  
    @NotNull(message = "El valor no puede ser nulo.")
    @NotEmpty(message = "El valor no puede estar vacío.")
    private String dataValue;
}
