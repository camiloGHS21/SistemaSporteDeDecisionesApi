package com.example.demo.domain.core;

import com.example.demo.domain.file.ValidatedDataRow;
import com.example.demo.domain.user.User;

import java.util.List;

public interface DatoIndicadorService {
    List<String> saveDatosIndicador(List<ValidatedDataRow> dataRows, Long fileId, User user);

    List<DatoIndicador> findByUserAndPais_Nombre_paisIgnoreCase(User user, String nombrePais);

    List<String> findDistinctIndicadoresByUser(User user);
}
