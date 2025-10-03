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
            // Trim whitespace from the country name for more flexible matching
            Optional<Pais> paisOpt = paisRepository.findByNombrePais(row.getPaisNombre().trim());
            if (paisOpt.isPresent()) {
                DatoIndicador indicador = new DatoIndicador();
                indicador.setPais(paisOpt.get());
                indicador.setTipo_indicador(row.getName());
                indicador.setValor(row.getValue());
                indicador.setAnio(row.getAnio());
                indicador.setFuente(row.getFuente());
                indicadores.add(indicador);
            } else {
                // Add error to the list instead of printing to console
                errors.add("Fila ignorada: País no encontrado en la base de datos: '" + row.getPaisNombre() + "'");
            }
        }

        // Only save if there are valid indicators to insert
        if (!indicadores.isEmpty()) {
            datoIndicadorRepository.saveAll(indicadores);
        }
        
        return errors;
    }

    @Override
    public List<DatoIndicador> findByPaisNombre(String nombrePais) {
        return datoIndicadorRepository.findByPais_NombrePaisIgnoreCase(nombrePais);
    }
}
