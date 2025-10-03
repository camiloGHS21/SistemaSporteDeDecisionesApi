package com.example.demo.domain.core;

import com.example.demo.domain.file.ValidatedDataRow;
import java.util.List;

public interface DatoIndicadorService {
    List<String> saveDatosIndicador(List<ValidatedDataRow> dataRows, Long fileId);

    List<DatoIndicador> findByPaisNombre(String nombrePais);
}
