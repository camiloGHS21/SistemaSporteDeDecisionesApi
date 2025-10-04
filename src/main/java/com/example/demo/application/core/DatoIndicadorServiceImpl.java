package com.example.demo.application.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.domain.core.DatoIndicador;
import com.example.demo.domain.core.DatoIndicadorRepository;
import com.example.demo.domain.core.DatoIndicadorService;
import com.example.demo.domain.core.Pais;
import com.example.demo.domain.core.PaisRepository;
import com.example.demo.domain.file.ValidatedDataRow;

@Service
public class DatoIndicadorServiceImpl implements DatoIndicadorService {

    private final DatoIndicadorRepository datoIndicadorRepository;
    private final PaisRepository paisRepository;

    public DatoIndicadorServiceImpl(DatoIndicadorRepository datoIndicadorRepository, PaisRepository paisRepository) {
        this.datoIndicadorRepository = datoIndicadorRepository;
        this.paisRepository = paisRepository;
    }

    @Override
    public List<String> saveDatosIndicador(List<ValidatedDataRow> dataRows, Long fileId) {
        List<String> errors = new ArrayList<>();
        List<DatoIndicador> indicadores = new ArrayList<>();
        for (ValidatedDataRow row : dataRows) {
            Optional<Pais> paisOpt = paisRepository.findByNombrePais(row.getPaisNombre().trim());
            if (paisOpt.isPresent()) {
                Pais pais = paisOpt.get();
                // Check for duplicates before adding
                boolean isDuplicate = datoIndicadorRepository.existsByPaisAndTipoIndicadorAndAnio(
                        pais, row.getName(), row.getAnio());

                if (isDuplicate) {
                    errors.add("Dato duplicado para País: '" + row.getPaisNombre() +
                            "', Indicador: '" + row.getName() + "', Año: " + row.getAnio());
                } else {
                    DatoIndicador indicador = new DatoIndicador();
                    indicador.setPais(pais);
                    indicador.setTipoIndicador(row.getName());
                    indicador.setValor(row.getValue());
                    indicador.setAnio(row.getAnio());
                    indicador.setFuente(row.getFuente());
                    indicadores.add(indicador);
                }
            } else {
                errors.add("Fila ignorada: País no encontrado en la base de datos: '" + row.getPaisNombre() + "'");
            }
        }

        if (!indicadores.isEmpty()) {
            datoIndicadorRepository.saveAll(indicadores);
        }
        
        return errors;
    }

    @Override
    public List<DatoIndicador> findByPaisNombre(String nombrePais) {
        return datoIndicadorRepository.findByPais_NombrePaisIgnoreCase(nombrePais);
    }

    @Override
    public List<String> findDistinctIndicadores() {
        return datoIndicadorRepository.findDistinctTipoIndicador();
    }
}
