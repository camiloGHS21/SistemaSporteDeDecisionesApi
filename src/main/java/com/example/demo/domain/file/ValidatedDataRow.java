package com.example.demo.domain.file;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ValidatedDataRow {

    @NotNull(message = "El nombre no puede ser nulo.")
    @NotEmpty(message = "El nombre no puede estar vacío.")
    @Size(max = 50, message = "El nombre no puede tener más de 50 caracteres.")
    private String name;

    @Min(value = 0, message = "El valor debe ser positivo.")
    private float value;
}
